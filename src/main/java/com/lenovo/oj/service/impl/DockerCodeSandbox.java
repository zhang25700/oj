package com.lenovo.oj.service.impl;

import com.lenovo.oj.config.DockerSandboxProperties;
import com.lenovo.oj.service.CodeSandbox;
import com.lenovo.oj.service.model.ExecuteCodeRequest;
import com.lenovo.oj.service.model.ExecuteCodeResponse;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
/**
 * 基于 Docker 的代码沙箱实现。
 *
 * 核心目标是把用户代码放到受限容器里编译和运行，通过 CPU、内存、网络、超时等限制
 * 控制风险范围，避免恶意代码直接影响宿主机。
 */
public class DockerCodeSandbox implements CodeSandbox {

    private static final int DEFAULT_TIME_LIMIT_MS = 3_000;
    private static final int DEFAULT_MEMORY_LIMIT_MB = 128;
    private static final int MIN_MEMORY_LIMIT_MB = 64;

    private final DockerSandboxProperties properties;
    private final AtomicLong lastDockerCheckAt = new AtomicLong(0L);
    private final ConcurrentMap<String, Boolean> imageReadyCache = new ConcurrentHashMap<>();

    private volatile boolean dockerAvailable;
    private volatile String dockerUnavailableReason = "Docker daemon is unavailable";

    @PostConstruct
    public void init() {
        // 启动时先做一次 Docker 可用性检查，并异步预热镜像，减少首个请求的冷启动时间。
        refreshDockerAvailability(true);
        if (properties.isPrewarmImages()) {
            Thread thread = new Thread(this::prewarmImages, "oj-sandbox-prewarm");
            thread.setDaemon(true);
            thread.start();
        }
    }

    @Override
    public ExecuteCodeResponse execute(ExecuteCodeRequest request) {
        SandboxSpec spec = resolveSpec(request.getLanguage());
        if (spec == null) {
            return failed("Unsupported language", "", 0, 0);
        }

        Path tempDir = null;
        String containerName = "oj-runner-" + UUID.randomUUID().toString().replace("-", "");
        long start = System.nanoTime();
        try {
            ensureDockerAvailable();
            ensureImageReady(spec.image());
            tempDir = Files.createTempDirectory("oj-docker-run-");
            prepareWorkspace(tempDir, spec, request);

            Path stdoutFile = tempDir.resolve("docker.stdout");
            Path stderrFile = tempDir.resolve("docker.stderr");

            // 这里显式分成 create / cp / start 三步，避免 Windows 下宿主机挂载导致的额外性能问题。
            createContainer(containerName, spec, request);
            copyWorkspace(tempDir, containerName);
            Process process = new ProcessBuilder(buildStartCommand(containerName))
                    .redirectOutput(stdoutFile.toFile())
                    .redirectError(stderrFile.toFile())
                    .start();

            long timeoutMs = calculateTimeoutMs(request.getTimeLimit());
            boolean finished = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
            if (!finished) {
                process.destroyForcibly();
                forceRemoveContainer(containerName);
                return failed("Time limit exceeded", "", (int) timeoutMs, memoryLimit(request));
            }

            String stdout = readFileIfExists(stdoutFile).trim();
            String stderr = readFileIfExists(stderrFile).trim();
            int exitCode = process.exitValue();
            int timeUsed = (int) Duration.ofNanos(System.nanoTime() - start).toMillis();
            int memoryUsed = memoryLimit(request);

            // 约定退出码：101 编译失败，124 运行超时，137 运行期被内存限制杀掉。
            if (exitCode == 0) {
                return ExecuteCodeResponse.builder()
                        .success(true)
                        .output(stdout)
                        .message("Run succeeded")
                        .timeUsed(timeUsed)
                        .memoryUsed(memoryUsed)
                        .build();
            }
            if (exitCode == 101) {
                return failed("Compile failed", stderr, timeUsed, memoryUsed);
            }
            if (exitCode == 124) {
                return failed("Time limit exceeded", stderr, timeUsed, memoryUsed);
            }
            if (exitCode == 137) {
                return failed("Memory limit exceeded", stderr, timeUsed, memoryUsed);
            }
            return failed("Runtime error", stderr.isBlank() ? stdout : stderr, timeUsed, memoryUsed);
        } catch (Exception e) {
            return failed("Sandbox failed", e.getMessage(), 0, memoryLimit(request));
        } finally {
            forceRemoveContainer(containerName);
            if (tempDir != null) {
                deleteDirectory(tempDir);
            }
        }
    }

    private void ensureDockerAvailable() {
        refreshDockerAvailability(false);
        if (!dockerAvailable) {
            throw new IllegalStateException(dockerUnavailableReason);
        }
    }

    private void refreshDockerAvailability(boolean force) {
        long now = System.currentTimeMillis();
        long lastChecked = lastDockerCheckAt.get();
        if (!force && now - lastChecked < properties.getAvailabilityCheckIntervalMs()) {
            return;
        }
        synchronized (lastDockerCheckAt) {
            lastChecked = lastDockerCheckAt.get();
            if (!force && now - lastChecked < properties.getAvailabilityCheckIntervalMs()) {
                return;
            }
            try {
                Process process = new ProcessBuilder("docker", "version", "--format", "{{.Server.Version}}")
                        .redirectErrorStream(true)
                        .start();
                boolean finished = process.waitFor(5, TimeUnit.SECONDS);
                if (!finished) {
                    process.destroyForcibly();
                    dockerAvailable = false;
                    dockerUnavailableReason = "Docker daemon check timed out";
                } else if (process.exitValue() != 0) {
                    dockerAvailable = false;
                    dockerUnavailableReason = readProcessOutput(process).trim();
                } else {
                    dockerAvailable = true;
                    dockerUnavailableReason = "";
                }
            } catch (Exception e) {
                dockerAvailable = false;
                dockerUnavailableReason = e.getMessage();
            }
            lastDockerCheckAt.set(System.currentTimeMillis());
        }
    }

    private void ensureImageReady(String image) throws Exception {
        if (Boolean.TRUE.equals(imageReadyCache.get(image))) {
            return;
        }
        synchronized (imageReadyCache) {
            if (Boolean.TRUE.equals(imageReadyCache.get(image))) {
                return;
            }
            Process inspectProcess = new ProcessBuilder("docker", "image", "inspect", image)
                    .redirectErrorStream(true)
                    .start();
            boolean finished = inspectProcess.waitFor(5, TimeUnit.SECONDS);
            if (!finished) {
                inspectProcess.destroyForcibly();
                throw new IllegalStateException("Docker image inspect timed out: " + image);
            }
            if (inspectProcess.exitValue() != 0) {
                throw new IllegalStateException("Docker image not found locally: " + image);
            }
            imageReadyCache.put(image, true);
        }
    }

    private void prewarmImages() {
        for (String image : List.of(properties.getJavaImage(), properties.getCppImage(), properties.getCImage())) {
            try {
                ensureDockerAvailable();
                if (Boolean.TRUE.equals(imageReadyCache.get(image))) {
                    continue;
                }
                Process process = new ProcessBuilder("docker", "pull", image)
                        .redirectErrorStream(true)
                        .start();
                boolean finished = process.waitFor(10, TimeUnit.MINUTES);
                if (!finished) {
                    process.destroyForcibly();
                    continue;
                }
                if (process.exitValue() == 0) {
                    imageReadyCache.put(image, true);
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void prepareWorkspace(Path tempDir, SandboxSpec spec, ExecuteCodeRequest request) throws IOException {
        Files.writeString(tempDir.resolve(spec.sourceFileName()), request.getCode(), StandardCharsets.UTF_8);
        Files.writeString(tempDir.resolve("input.txt"), request.getInput() == null ? "" : request.getInput(), StandardCharsets.UTF_8);
        Files.writeString(tempDir.resolve("run.sh"), buildRunScript(spec, request), StandardCharsets.UTF_8);
    }

    private void createContainer(String containerName, SandboxSpec spec, ExecuteCodeRequest request) throws Exception {
        Process process = new ProcessBuilder(buildCreateCommand(containerName, spec, request))
                .redirectErrorStream(true)
                .start();
        boolean finished = process.waitFor(15, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new IllegalStateException("Docker create timed out");
        }
        if (process.exitValue() != 0) {
            throw new IllegalStateException(readProcessOutput(process).trim());
        }
    }

    private void copyWorkspace(Path tempDir, String containerName) throws Exception {
        String sourcePath = tempDir.toAbsolutePath().toString() + System.getProperty("file.separator") + ".";
        Process process = new ProcessBuilder("docker", "cp", sourcePath, containerName + ":" + properties.getWorkspace())
                .redirectErrorStream(true)
                .start();
        boolean finished = process.waitFor(15, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new IllegalStateException("Docker copy timed out");
        }
        if (process.exitValue() != 0) {
            throw new IllegalStateException(readProcessOutput(process).trim());
        }
    }

    private List<String> buildCreateCommand(String containerName, SandboxSpec spec, ExecuteCodeRequest request) {
        List<String> command = new ArrayList<>();
        command.add("docker");
        command.add("create");
        command.add("--name");
        command.add(containerName);
        command.add("--network");
        command.add("none");
        command.add("--cpus");
        command.add(String.valueOf(properties.getCpus()));
        command.add("--memory");
        command.add(containerMemoryLimit(request) + "m");
        command.add("--memory-swap");
        command.add(containerMemoryLimit(request) + "m");
        command.add("--pids-limit");
        command.add(String.valueOf(properties.getPidsLimit()));
        command.add("--security-opt");
        command.add("no-new-privileges");
        command.add("--cap-drop");
        command.add("ALL");
        command.add("--tmpfs");
        command.add("/tmp:rw,noexec,nosuid,size=64m");
        command.add("-w");
        command.add(properties.getWorkspace());
        command.add(spec.image());
        command.add("sh");
        command.add("run.sh");
        return command;
    }

    private List<String> buildStartCommand(String containerName) {
        return List.of("docker", "start", "-a", containerName);
    }

    private String buildRunScript(SandboxSpec spec, ExecuteCodeRequest request) {
        String lineSeparator = "\n";
        int javaHeapMb = Math.max(32, memoryLimit(request) - 32);
        String runtimeLimitSeconds = formatTimeoutSeconds(request.getTimeLimit());
        String nativeMemoryLimitKb = String.valueOf((long) memoryLimit(request) * 1024L);
        // 编译阶段给足容器内存，运行阶段再按题目限制收紧；否则 C++ 编译器本身就可能被提前杀掉。
        return switch (spec.language()) {
            case "java" -> String.join(lineSeparator,
                    "#!/bin/sh",
                    "javac -encoding UTF-8 Main.java 1>/tmp/compile.out 2>/tmp/compile.err",
                    "compile_status=$?",
                    "if [ \"$compile_status\" -ne 0 ]; then",
                    "  cat /tmp/compile.err 1>&2",
                    "  exit 101",
                    "fi",
                    "timeout -s KILL " + runtimeLimitSeconds + " java -Xms16m -Xmx" + javaHeapMb + "m Main < input.txt",
                    "run_status=$?",
                    "if [ \"$run_status\" -eq 124 ] || [ \"$run_status\" -eq 137 ]; then",
                    "  exit 124",
                    "fi",
                    "exit \"$run_status\"",
                    ""
            );
            case "cpp" -> String.join(lineSeparator,
                    "#!/bin/sh",
                    "g++ -std=gnu++23 -O2 -pipe Main.cpp -o Main 1>/tmp/compile.out 2>/tmp/compile.err",
                    "compile_status=$?",
                    "if [ \"$compile_status\" -ne 0 ]; then",
                    "  cat /tmp/compile.err 1>&2",
                    "  exit 101",
                    "fi",
                    "ulimit -v " + nativeMemoryLimitKb,
                    "timeout -s KILL " + runtimeLimitSeconds + " ./Main < input.txt",
                    "run_status=$?",
                    "if [ \"$run_status\" -eq 124 ] || [ \"$run_status\" -eq 137 ]; then",
                    "  exit 124",
                    "fi",
                    "exit \"$run_status\"",
                    ""
            );
            case "c" -> String.join(lineSeparator,
                    "#!/bin/sh",
                    "gcc -std=gnu17 -O2 -pipe Main.c -o Main 1>/tmp/compile.out 2>/tmp/compile.err",
                    "compile_status=$?",
                    "if [ \"$compile_status\" -ne 0 ]; then",
                    "  cat /tmp/compile.err 1>&2",
                    "  exit 101",
                    "fi",
                    "ulimit -v " + nativeMemoryLimitKb,
                    "timeout -s KILL " + runtimeLimitSeconds + " ./Main < input.txt",
                    "run_status=$?",
                    "if [ \"$run_status\" -eq 124 ] || [ \"$run_status\" -eq 137 ]; then",
                    "  exit 124",
                    "fi",
                    "exit \"$run_status\"",
                    ""
            );
            default -> throw new IllegalArgumentException("Unsupported language");
        };
    }

    private long calculateTimeoutMs(Integer timeLimitMs) {
        long base = timeLimitMs == null ? DEFAULT_TIME_LIMIT_MS : Math.max(500L, timeLimitMs.longValue());
        return base + properties.getCompileTimeoutMs() + properties.getExtraTimeoutMs() + properties.getStartupTimeoutMs();
    }

    private int memoryLimit(ExecuteCodeRequest request) {
        if (request == null || request.getMemoryLimit() == null) {
            return DEFAULT_MEMORY_LIMIT_MB;
        }
        return Math.max(MIN_MEMORY_LIMIT_MB, request.getMemoryLimit());
    }

    private int containerMemoryLimit(ExecuteCodeRequest request) {
        return Math.max(memoryLimit(request), properties.getCompileMemoryLimitMb());
    }

    private SandboxSpec resolveSpec(String language) {
        if (language == null) {
            return null;
        }
        return switch (language.toLowerCase(Locale.ROOT)) {
            case "java" -> new SandboxSpec("java", properties.getJavaImage(), "Main.java");
            case "cpp" -> new SandboxSpec("cpp", properties.getCppImage(), "Main.cpp");
            case "c" -> new SandboxSpec("c", properties.getCImage(), "Main.c");
            default -> null;
        };
    }

    private String readFileIfExists(Path path) throws IOException {
        if (!Files.exists(path)) {
            return "";
        }
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    private String readProcessOutput(Process process) throws IOException {
        return new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private ExecuteCodeResponse failed(String defaultMessage, String detail, int timeUsed, int memoryUsed) {
        return ExecuteCodeResponse.builder()
                .success(false)
                .output("")
                .message(detail == null || detail.isBlank() ? defaultMessage : detail)
                .timeUsed(timeUsed)
                .memoryUsed(memoryUsed)
                .build();
    }

    private String formatTimeoutSeconds(Integer timeLimitMs) {
        long timeoutMs = timeLimitMs == null ? DEFAULT_TIME_LIMIT_MS : Math.max(500L, timeLimitMs.longValue());
        return String.format(Locale.ROOT, "%.3fs", timeoutMs / 1000.0D);
    }

    private void forceRemoveContainer(String containerName) {
        try {
            Process process = new ProcessBuilder("docker", "rm", "-f", containerName)
                    .redirectErrorStream(true)
                    .start();
            process.waitFor(5, TimeUnit.SECONDS);
        } catch (Exception ignored) {
        }
    }

    private void deleteDirectory(Path path) {
        try {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .forEach(file -> {
                        try {
                            Files.deleteIfExists(file);
                        } catch (IOException ignored) {
                        }
                    });
        } catch (IOException ignored) {
        }
    }

    private record SandboxSpec(String language, String image, String sourceFileName) {
    }
}

package com.lenovo.oj.service.impl;

import com.lenovo.oj.service.CodeSandbox;
import com.lenovo.oj.service.model.ExecuteCodeRequest;
import com.lenovo.oj.service.model.ExecuteCodeResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class DockerCodeSandbox implements CodeSandbox {

    private static final String WINDOWS_GPP = "D:\\mingw64\\bin\\g++.exe";
    private static final String WINDOWS_GCC = "D:\\mingw64\\bin\\gcc.exe";

    @Override
    public ExecuteCodeResponse execute(ExecuteCodeRequest request) {
        if ("java".equalsIgnoreCase(request.getLanguage())) {
            return runJava(request);
        }
        if ("cpp".equalsIgnoreCase(request.getLanguage())) {
            return runNative(
                    request,
                    "Main.cpp",
                    findNativeCompiler("g++", WINDOWS_GPP),
                    List.of("-std=gnu++23", "-O2", "-Wall", "-o", "Main.exe", "Main.cpp"),
                    "Main.exe"
            );
        }
        if ("c".equalsIgnoreCase(request.getLanguage())) {
            return runNative(
                    request,
                    "Main.c",
                    findNativeCompiler("gcc", WINDOWS_GCC),
                    List.of("-std=gnu17", "-O2", "-Wall", "-o", "Main.exe", "Main.c"),
                    "Main.exe"
            );
        }
        return ExecuteCodeResponse.builder()
                .success(false)
                .output("")
                .message("Unsupported language")
                .timeUsed(0)
                .memoryUsed(0)
                .build();
    }

    private ExecuteCodeResponse runJava(ExecuteCodeRequest request) {
        Path tempDir = null;
        long start = System.nanoTime();
        try {
            tempDir = Files.createTempDirectory("oj-java-run-");
            Files.writeString(tempDir.resolve("Main.java"), request.getCode(), StandardCharsets.UTF_8);

            Process compileProcess = new ProcessBuilder(javaBin("javac"), "-encoding", "UTF-8", "Main.java")
                    .directory(tempDir.toFile())
                    .start();
            String compileError = waitAndRead(compileProcess, 10_000);
            if (compileProcess.exitValue() != 0) {
                return failed("Compile failed", compileError, 0);
            }

            Process runProcess = new ProcessBuilder(javaBin("java"), "Main")
                    .directory(tempDir.toFile())
                    .start();
            return finishProcess(runProcess, request, start);
        } catch (Exception e) {
            return failed(e.getMessage(), e.getMessage(), 0);
        } finally {
            if (tempDir != null) {
                deleteDirectory(tempDir);
            }
        }
    }

    private ExecuteCodeResponse runNative(ExecuteCodeRequest request, String sourceName, String compiler, List<String> compileArgs, String executableName) {
        Path tempDir = null;
        long start = System.nanoTime();
        try {
            tempDir = Files.createTempDirectory("oj-native-run-");
            Files.writeString(tempDir.resolve(sourceName), request.getCode(), StandardCharsets.UTF_8);

            List<String> compileCommand = new ArrayList<>();
            compileCommand.add(compiler);
            compileCommand.addAll(compileArgs);
            Process compileProcess = new ProcessBuilder(compileCommand)
                    .directory(tempDir.toFile())
                    .start();
            String compileError = waitAndRead(compileProcess, 10_000);
            if (compileProcess.exitValue() != 0) {
                return failed("Compile failed", compileError, 0);
            }

            Process runProcess = new ProcessBuilder(tempDir.resolve(executableName).toString())
                    .directory(tempDir.toFile())
                    .start();
            return finishProcess(runProcess, request, start);
        } catch (Exception e) {
            return failed(e.getMessage(), e.getMessage(), 0);
        } finally {
            if (tempDir != null) {
                deleteDirectory(tempDir);
            }
        }
    }

    private ExecuteCodeResponse finishProcess(Process runProcess, ExecuteCodeRequest request, long start) throws Exception {
        if (request.getInput() != null) {
            runProcess.getOutputStream().write(request.getInput().getBytes(StandardCharsets.UTF_8));
            runProcess.getOutputStream().flush();
        }
        runProcess.getOutputStream().close();

        long timeout = request.getTimeLimit() == null ? 3000L : Math.max(500L, request.getTimeLimit().longValue());
        boolean finished = runProcess.waitFor(timeout, TimeUnit.MILLISECONDS);
        if (!finished) {
            runProcess.destroyForcibly();
            return ExecuteCodeResponse.builder()
                    .success(false)
                    .output("")
                    .message("Time limit exceeded")
                    .timeUsed((int) timeout)
                    .memoryUsed(0)
                    .build();
        }

        String stdout = readStream(runProcess.getInputStream()).trim();
        String stderr = readStream(runProcess.getErrorStream()).trim();
        if (runProcess.exitValue() != 0) {
            return ExecuteCodeResponse.builder()
                    .success(false)
                    .output(stdout)
                    .message(stderr.isBlank() ? "Runtime error" : stderr)
                    .timeUsed((int) Duration.ofNanos(System.nanoTime() - start).toMillis())
                    .memoryUsed(0)
                    .build();
        }

        return ExecuteCodeResponse.builder()
                .success(true)
                .output(stdout)
                .message("Run succeeded")
                .timeUsed((int) Duration.ofNanos(System.nanoTime() - start).toMillis())
                .memoryUsed(0)
                .build();
    }

    private ExecuteCodeResponse failed(String defaultMessage, String detail, int timeUsed) {
        return ExecuteCodeResponse.builder()
                .success(false)
                .output("")
                .message(detail == null || detail.isBlank() ? defaultMessage : detail)
                .timeUsed(timeUsed)
                .memoryUsed(0)
                .build();
    }

    private String waitAndRead(Process process, long timeoutMs) throws Exception {
        boolean finished = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
        if (!finished) {
            process.destroyForcibly();
            return "Compile timeout";
        }
        return readStream(process.getErrorStream()).trim();
    }

    private String readStream(InputStream inputStream) throws IOException {
        try (inputStream; ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            inputStream.transferTo(outputStream);
            return outputStream.toString(StandardCharsets.UTF_8);
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

    private String javaBin(String command) {
        String javaHome = System.getenv("JAVA_HOME");
        if (javaHome == null || javaHome.isBlank()) {
            return command;
        }
        String suffix = System.getProperty("os.name").toLowerCase().contains("win") ? ".exe" : "";
        return Path.of(javaHome, "bin", command + suffix).toString();
    }

    private String findNativeCompiler(String command, String windowsPreferredPath) {
        if (System.getProperty("os.name").toLowerCase().contains("win") && Files.exists(Path.of(windowsPreferredPath))) {
            return windowsPreferredPath;
        }
        String gccHome = System.getenv("MINGW_HOME");
        if (gccHome != null && !gccHome.isBlank()) {
            return Path.of(gccHome, "bin", command + ".exe").toString();
        }
        return command;
    }
}

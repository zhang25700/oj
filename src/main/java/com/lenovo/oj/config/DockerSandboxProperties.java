package com.lenovo.oj.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "oj.sandbox.docker")
/**
 * Docker 判题沙箱配置。
 *
 * 这些参数用于控制镜像选择、CPU / 内存限制、镜像预热、Docker 可用性检查间隔等，
 * 目的是把“沙箱策略”从代码里抽离出来，方便按部署环境调优。
 */
public class DockerSandboxProperties {

    private String javaImage = "eclipse-temurin:17-jdk";

    private String cppImage = "gcc:14";

    private String cImage = "gcc:14";

    private String workspace = "/workspace";

    private double cpus = 1.0D;

    private int compileTimeoutMs = 10_000;

    private int extraTimeoutMs = 2_000;

    private int pidsLimit = 128;

    private long availabilityCheckIntervalMs = 30_000L;

    private boolean prewarmImages = true;

    private int startupTimeoutMs = 20_000;

    private int compileMemoryLimitMb = 512;
}

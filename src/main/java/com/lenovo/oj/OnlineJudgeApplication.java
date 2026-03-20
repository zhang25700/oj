package com.lenovo.oj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.lenovo.oj.mapper")
@SpringBootApplication
/**
 * Spring Boot 启动入口。
 *
 * 整个 OJ 后端以这个应用为根容器启动，自动装配控制器、服务、MQ 监听器、
 * Redis / MySQL / RabbitMQ 配置以及 Docker 沙箱相关组件。
 */
public class OnlineJudgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineJudgeApplication.class, args);
    }
}

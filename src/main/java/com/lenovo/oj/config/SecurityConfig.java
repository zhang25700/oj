package com.lenovo.oj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
/**
 * Spring Security 最小化配置。
 *
 * 当前项目不使用 Spring Security 自带的登录页和 Session 机制，
 * 而是自行实现基于 Redis Token 的登录态管理。
 * 因此这里主要做两件事：
 * 1. 关闭默认表单登录和 CSRF
 * 2. 暴露 PasswordEncoder 给用户注册 / 登录使用
 */
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 所有请求先放行，具体登录校验交给自定义拦截器和业务服务处理。
        http.csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .formLogin(form -> form.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 密码落库前统一用 BCrypt 加密，避免明文存储。
        return new BCryptPasswordEncoder();
    }
}

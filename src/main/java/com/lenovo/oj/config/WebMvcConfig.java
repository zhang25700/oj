package com.lenovo.oj.config;

import com.lenovo.oj.auth.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
/**
 * Spring MVC 扩展配置。
 *
 * 这里主要负责：
 * - 注册登录态拦截器
 * - 放开前后端分离开发时需要的跨域访问
 */
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 所有接口都走拦截器，这样后续业务层随时可以拿到当前用户上下文。
        registry.addInterceptor(authInterceptor).addPathPatterns("/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 开发阶段前端通常运行在 5173 端口，需要跨域访问 8101 后端接口。
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}

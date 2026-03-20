package com.lenovo.oj.auth;

import com.lenovo.oj.common.ErrorCode;
import com.lenovo.oj.constant.RedisConstant;
import com.lenovo.oj.constant.UserConstant;
import com.lenovo.oj.exception.BusinessException;
import com.lenovo.oj.model.entity.User;
import com.lenovo.oj.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
/**
 * 基于 Redis Token 的轻量鉴权拦截器。
 *
 * 拦截器的职责不是强制所有请求必须登录，而是：
 * 1. 如果请求头里带了 token，就尝试解析并加载当前用户。
 * 2. 如果没有 token，则允许继续往下走，由具体业务接口决定是否要求登录。
 *
 * 这样可以同时支持“公开接口”和“登录后接口”，避免在控制层重复写用户解析逻辑。
 */
public class AuthInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;
    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // token 存在时才尝试从 Redis 里恢复用户上下文；没有 token 的请求直接放行。
        String token = request.getHeader(UserConstant.LOGIN_TOKEN);
        if (token == null || token.isBlank()) {
            return true;
        }
        String userIdValue = stringRedisTemplate.opsForValue().get(RedisConstant.LOGIN_USER_PREFIX + token);
        if (userIdValue == null) {
            return true;
        }
        User user = userService.getById(Long.parseLong(userIdValue));
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        LoginUserHolder.set(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // ThreadLocal 必须在请求结束后清理，避免线程复用时把上一个请求的用户串到下一个请求。
        LoginUserHolder.remove();
    }
}

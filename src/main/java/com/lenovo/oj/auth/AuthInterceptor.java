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
public class AuthInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;
    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
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
        LoginUserHolder.remove();
    }
}

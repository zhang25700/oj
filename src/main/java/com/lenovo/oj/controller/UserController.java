package com.lenovo.oj.controller;

import com.lenovo.oj.common.BaseResponse;
import com.lenovo.oj.common.ResultUtils;
import com.lenovo.oj.model.dto.user.UserLoginRequest;
import com.lenovo.oj.model.dto.user.UserRegisterRequest;
import com.lenovo.oj.model.entity.User;
import com.lenovo.oj.model.vo.LoginUserVO;
import com.lenovo.oj.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
/**
 * 用户相关接口。
 *
 * 负责注册、登录和获取当前登录用户信息。
 * 登录态本身由 Redis Token + 拦截器维护。
 */
public class UserController {

    private final UserService userService;

    // 注册成功后只返回 userId，登录态不会在注册时自动创建。
    @PostMapping("/register")
    public BaseResponse<Long> register(@Valid @RequestBody UserRegisterRequest request) {
        return ResultUtils.success(userService.register(request));
    }

    // 登录成功后返回 token，前端后续请求放到 Authorization 请求头即可。
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> login(@Valid @RequestBody UserLoginRequest request) {
        return ResultUtils.success(userService.login(request));
    }

    // 获取当前登录用户的脱敏信息，用于前端恢复导航栏和页面权限状态。
    @GetMapping("/me")
    public BaseResponse<User> getCurrentUser() {
        User user = userService.getLoginUser();
        user.setUserPassword(null);
        return ResultUtils.success(user);
    }
}

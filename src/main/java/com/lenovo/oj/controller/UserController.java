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
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> register(@Valid @RequestBody UserRegisterRequest request) {
        return ResultUtils.success(userService.register(request));
    }

    @PostMapping("/login")
    public BaseResponse<LoginUserVO> login(@Valid @RequestBody UserLoginRequest request) {
        return ResultUtils.success(userService.login(request));
    }

    @GetMapping("/me")
    public BaseResponse<User> getCurrentUser() {
        User user = userService.getLoginUser();
        user.setUserPassword(null);
        return ResultUtils.success(user);
    }
}

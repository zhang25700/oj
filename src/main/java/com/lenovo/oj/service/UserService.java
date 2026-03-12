package com.lenovo.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lenovo.oj.model.dto.user.UserLoginRequest;
import com.lenovo.oj.model.dto.user.UserRegisterRequest;
import com.lenovo.oj.model.entity.User;
import com.lenovo.oj.model.vo.LoginUserVO;

public interface UserService extends IService<User> {

    Long register(UserRegisterRequest request);

    LoginUserVO login(UserLoginRequest request);

    User getLoginUser();

    void increaseSolvedCount(Long userId);
}

package com.lenovo.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lenovo.oj.model.dto.user.UserLoginRequest;
import com.lenovo.oj.model.dto.user.UserRegisterRequest;
import com.lenovo.oj.model.entity.User;
import com.lenovo.oj.model.vo.LoginUserVO;

public interface UserService extends IService<User> {

    /**
     * 注册用户。
     */
    Long register(UserRegisterRequest request);

    /**
     * 登录并创建 Redis token。
     */
    LoginUserVO login(UserLoginRequest request);

    /**
     * 获取当前登录用户。
     */
    User getLoginUser();

    /**
     * 用户 AC 后增加已解题数并更新最后通过时间。
     */
    void increaseSolvedCount(Long userId);
}

package com.lenovo.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lenovo.oj.auth.LoginUserHolder;
import com.lenovo.oj.common.ErrorCode;
import com.lenovo.oj.constant.RedisConstant;
import com.lenovo.oj.constant.UserConstant;
import com.lenovo.oj.exception.BusinessException;
import com.lenovo.oj.mapper.UserMapper;
import com.lenovo.oj.model.dto.user.UserLoginRequest;
import com.lenovo.oj.model.dto.user.UserRegisterRequest;
import com.lenovo.oj.model.entity.User;
import com.lenovo.oj.model.vo.LoginUserVO;
import com.lenovo.oj.service.UserService;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.StringRedisTemplate;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(UserRegisterRequest request) {
        long count = count(new LambdaQueryWrapper<User>().eq(User::getUserAccount, request.getUserAccount()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }
        User user = new User();
        user.setUserAccount(request.getUserAccount());
        user.setUserName(request.getUserName());
        user.setUserPassword(passwordEncoder.encode(request.getUserPassword()));
        user.setUserRole(UserConstant.USER_ROLE);
        user.setSolvedCount(0);
        save(user);
        return user.getId();
    }

    @Override
    public LoginUserVO login(UserLoginRequest request) {
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUserAccount, request.getUserAccount()));
        if (user == null || !passwordEncoder.matches(request.getUserPassword(), user.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(
                RedisConstant.LOGIN_USER_PREFIX + token,
                String.valueOf(user.getId()),
                UserConstant.LOGIN_EXPIRE_SECONDS,
                TimeUnit.SECONDS
        );
        return new LoginUserVO(user.getId(), user.getUserName(), user.getUserRole(), token);
    }

    @Override
    public User getLoginUser() {
        User user = LoginUserHolder.get();
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void increaseSolvedCount(Long userId) {
        User user = getById(userId);
        if (user == null) {
            return;
        }
        user.setSolvedCount((user.getSolvedCount() == null ? 0 : user.getSolvedCount()) + 1);
        user.setLastAcceptedTime(LocalDateTime.now());
        updateById(user);
    }
}

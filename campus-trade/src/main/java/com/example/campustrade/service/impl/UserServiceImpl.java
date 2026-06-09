package com.example.campustrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.campustrade.common.RedisKeys;
import com.example.campustrade.convert.UserConvert;
import com.example.campustrade.enums.UserStatus;
import com.example.campustrade.utils.JwtUtils;
import com.example.campustrade.vo.LoginVO;
import com.example.campustrade.vo.UserVO;
import com.example.campustrade.common.BusinessException;
import com.example.campustrade.dto.LoginDTO;
import com.example.campustrade.dto.RegisterDTO;
import com.example.campustrade.entity.UserDO;
import com.example.campustrade.mapper.UserMapper;
import com.example.campustrade.service.UserService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final StringRedisTemplate stringRedisTemplate;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, StringRedisTemplate stringRedisTemplate) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    //注册用户
    @Override
    public void register(RegisterDTO registerDTO) {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getUsername,registerDTO.getUsername());
        UserDO userDO = userMapper.selectOne(wrapper);
        if (userDO != null){
            throw new BusinessException("该用户名已存在");
        }
        UserDO newUser = new UserDO();
        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
        newUser.setUsername(registerDTO.getUsername());
        newUser.setPassword(encodedPassword);
        newUser.setNickname(registerDTO.getUsername());
        userMapper.insert(newUser);
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        String failKey = RedisKeys.LOGIN_FAIL_KEY_PREFIX + loginDTO.getUsername();
        String failCountStr = stringRedisTemplate.opsForValue().get(failKey);
        if(failCountStr !=null && Integer.parseInt(failCountStr)>=5){
            throw new BusinessException("登录失败次数过多，请十分钟后再试");
        }

        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getUsername,loginDTO.getUsername());
        UserDO userDO = userMapper.selectOne(wrapper);
        if(userDO == null){
            stringRedisTemplate.opsForValue().increment(failKey);
            stringRedisTemplate.expire(failKey, Duration.ofMinutes(10));
            throw new BusinessException("密码或者用户名输入错误"); //先检查数据库 没有userDO就说明没注册
        }
        if(!passwordEncoder.matches(loginDTO.getPassword(),userDO.getPassword())){
            stringRedisTemplate.opsForValue().increment(failKey);
            stringRedisTemplate.expire(failKey, Duration.ofMinutes(10));
            throw new BusinessException("密码或者用户名输入错误");
        }
        if (userDO.getStatus().equals(UserStatus.DISABLED.getCode())){
            throw new BusinessException("用户处于禁用状态");
        }

        stringRedisTemplate.delete(failKey);
        UserVO userVO = UserConvert.convertToVO(userDO);

        LoginVO loginVO = new LoginVO();
        loginVO.setUser(userVO);
        loginVO.setToken(JwtUtils.generateToken(userDO.getId()));
        return loginVO;
    }
}

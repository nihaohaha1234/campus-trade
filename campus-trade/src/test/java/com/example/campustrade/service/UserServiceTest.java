package com.example.campustrade.service;

import com.example.campustrade.common.BusinessException;
import com.example.campustrade.component.JwtUtils;
import com.example.campustrade.dto.LoginDTO;
import com.example.campustrade.dto.RegisterDTO;
import com.example.campustrade.entity.UserDO;
import com.example.campustrade.enums.UserStatus;
import com.example.campustrade.mapper.UserMapper;
import com.example.campustrade.service.impl.UserServiceImpl;
import com.example.campustrade.vo.LoginVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ValueOperations<String,String> valueOperations;
    @Mock
    private JwtUtils jwtUtils;

    UserServiceImpl userService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        userService = new UserServiceImpl(userMapper,passwordEncoder,stringRedisTemplate,jwtUtils);
    }

    @Test
    void 登录次数过多_五分钟后再试(){
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testUser");
        dto.setPassword("123456");
        when(stringRedisTemplate.opsForValue().get(any())).thenReturn(String.valueOf(6));
        BusinessException exception = assertThrows(BusinessException.class,()->{
            userService.login(dto);
            }
        );
        assertTrue(exception.getMessage().contains("登录失败次数过多，请十分钟后再试"));
    }

    @Test
    void 成功登录(){
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testUser");
        dto.setPassword("123456");
        UserDO userDO = new UserDO();
        userDO.setUsername("testUser");
        userDO.setPassword("123456");
        userDO.setStatus(UserStatus.NORMAL.getCode());
        when(stringRedisTemplate.opsForValue().get(any())).thenReturn(String.valueOf(1));
        when(userMapper.selectOne(any())).thenReturn(userDO);
        when(passwordEncoder.matches(dto.getPassword(),userDO.getPassword())).thenReturn(true);
        when(jwtUtils.generateToken(any())).thenReturn("testToken");
        LoginVO loginVO = userService.login(dto);
        verify(stringRedisTemplate).delete(any(String.class));
        assertNotNull(loginVO);
        assertEquals(loginVO.getToken(),"testToken");
        assertEquals(loginVO.getUser().getUsername(),"testUser");
    }

    @Test
    void 用户不存�?){
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testUser");
        dto.setPassword("123456");
        when(stringRedisTemplate.opsForValue().get(any())).thenReturn(String.valueOf(1));
        when(userMapper.selectOne(any())).thenReturn(null);
        BusinessException exception = assertThrows(BusinessException.class,()->{
            userService.login(dto);
        });
        assertTrue(exception.getMessage().contains("密码或者用户名输入错误"));
        verify(valueOperations).increment(any());
        verify(stringRedisTemplate).expire(any(),any());
    }

    @Test
    void 密码错误(){
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testUser");
        dto.setPassword("123456");
        UserDO userDO = new UserDO();
        userDO.setUsername("testUser");
        userDO.setPassword("654321");
        userDO.setStatus(UserStatus.NORMAL.getCode());
        when(stringRedisTemplate.opsForValue().get(any())).thenReturn(String.valueOf(1));
        when(userMapper.selectOne(any())).thenReturn(userDO);
        when(passwordEncoder.matches(any(),any())).thenReturn(false);
        BusinessException exception = assertThrows(BusinessException.class,()->{
            userService.login(dto);
        });
        assertTrue(exception.getMessage().contains("密码或者用户名输入错误"));
        verify(valueOperations).increment(any());
        verify(stringRedisTemplate).expire(any(),any());
    }

    @Test
    void 用户已被禁用(){
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testUser");
        dto.setPassword("123456");
        UserDO userDO = new UserDO();
        userDO.setUsername("testUser");
        userDO.setPassword("123456");
        userDO.setStatus(UserStatus.DISABLED.getCode());
        when(stringRedisTemplate.opsForValue().get(any())).thenReturn(String.valueOf(1));
        when(userMapper.selectOne(any())).thenReturn(userDO);
        when(passwordEncoder.matches(dto.getPassword(),userDO.getPassword())).thenReturn(true);
        BusinessException exception = assertThrows(BusinessException.class,()->{
            userService.login(dto);
        });
        assertTrue(exception.getMessage().contains("用户处于禁用状�?));
        verify(stringRedisTemplate,never()).delete(any(String.class));
    }

    @Test
    void 注册成功(){
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setPassword("123456");
        registerDTO.setUsername("testUser");
        when(userMapper.selectOne(any())).thenReturn(null);
        when(passwordEncoder.encode(any())).thenReturn("testPassword");
        userService.register(registerDTO);
        verify(userMapper,times(1)).insert(any(UserDO.class));
    }

    @Test
    void 注册失败_账号已存�?){
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setPassword("123456");
        registerDTO.setUsername("testUser");
        when(userMapper.selectOne(any())).thenReturn(new UserDO());
        BusinessException exception = assertThrows(BusinessException.class,()->{
            userService.register(registerDTO);
        });
        assertTrue(exception.getMessage().contains("该用户名已存�?));
        verify(userMapper,never()).insert(any(UserDO.class));
    }
}

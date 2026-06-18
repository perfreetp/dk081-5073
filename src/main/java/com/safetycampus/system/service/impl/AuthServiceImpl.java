package com.safetycampus.system.service.impl;

import com.safetycampus.common.context.LoginUser;
import com.safetycampus.common.context.UserContext;
import com.safetycampus.common.enums.RoleTypeEnum;
import com.safetycampus.common.exception.BusinessException;
import com.safetycampus.common.result.ResultCode;
import com.safetycampus.common.utils.JwtUtil;
import com.safetycampus.system.dto.LoginDTO;
import com.safetycampus.system.dto.LoginVO;
import com.safetycampus.system.entity.SysUser;
import com.safetycampus.system.service.AuthService;
import com.safetycampus.system.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private JwtUtil jwtUtil;

    @Value("${jwt.expire}")
    private Long expire;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        SysUser user = sysUserService.getByUsername(loginDTO.getUsername());
        if (user == null) {
            throw BusinessException.of(ResultCode.PASSWORD_ERROR);
        }
        if (user.getStatus() == null || user.getStatus() == 0) {
            throw BusinessException.of(ResultCode.ACCOUNT_LOCKED);
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw BusinessException.of(ResultCode.PASSWORD_ERROR);
        }

        LoginUser loginUser = LoginUser.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .phone(user.getPhone())
                .roleType(RoleTypeEnum.getByCode(user.getRoleType()))
                .schoolId(user.getSchoolId())
                .build();

        String token = jwtUtil.createToken(loginUser);

        return LoginVO.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(expire)
                .build();
    }

    @Override
    public void logout() {
        UserContext.remove();
    }

    @Override
    public LoginUser getCurrentUser() {
        return UserContext.getLoginUser();
    }
}

package com.safetycampus.system.controller;

import com.safetycampus.common.annotation.SysLog;
import com.safetycampus.common.context.LoginUser;
import com.safetycampus.common.result.Result;
import com.safetycampus.system.dto.LoginDTO;
import com.safetycampus.system.dto.LoginVO;
import com.safetycampus.system.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证接口", description = "用户登录、登出、获取当前用户信息")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    @SysLog(module = "认证管理", operation = "用户登录")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = authService.login(loginDTO);
        return Result.success("登录成功", loginVO);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<LoginUser> getInfo() {
        LoginUser loginUser = authService.getCurrentUser();
        return Result.success(loginUser);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    @SysLog(module = "认证管理", operation = "用户登出")
    public Result<Void> logout() {
        authService.logout();
        return Result.success("登出成功", null);
    }
}

package com.safetycampus.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.safetycampus.common.annotation.SysLog;
import com.safetycampus.common.result.Result;
import com.safetycampus.system.dto.SysUserDTO;
import com.safetycampus.system.dto.SysUserQueryDTO;
import com.safetycampus.system.entity.SysUser;
import com.safetycampus.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户管理接口", description = "用户增删改查、密码重置等功能")
@RestController
@RequestMapping("/api/system/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @Operation(summary = "分页查询用户列表")
    @GetMapping("/page")
    public Result<IPage<SysUser>> page(SysUserQueryDTO queryDTO) {
        IPage<SysUser> page = sysUserService.selectPage(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public Result<SysUser> getDetail(@Parameter(description = "用户ID") @PathVariable Long id) {
        SysUser user = sysUserService.getDetail(id);
        return Result.success(user);
    }

    @Operation(summary = "新增用户")
    @PostMapping
    @SysLog(module = "用户管理", operation = "新增用户")
    public Result<Boolean> add(@Valid @RequestBody SysUserDTO dto) {
        boolean result = sysUserService.saveUser(dto);
        return Result.success("新增成功", result);
    }

    @Operation(summary = "修改用户")
    @PutMapping
    @SysLog(module = "用户管理", operation = "修改用户")
    public Result<Boolean> update(@Valid @RequestBody SysUserDTO dto) {
        boolean result = sysUserService.updateUser(dto);
        return Result.success("修改成功", result);
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    @SysLog(module = "用户管理", operation = "删除用户")
    public Result<Boolean> delete(@Parameter(description = "用户ID") @PathVariable Long id) {
        boolean result = sysUserService.deleteUser(id);
        return Result.success("删除成功", result);
    }

    @Operation(summary = "重置密码")
    @PutMapping("/{id}/reset-password")
    @SysLog(module = "用户管理", operation = "重置密码")
    public Result<Boolean> resetPassword(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "新密码") @RequestParam String newPassword) {
        boolean result = sysUserService.resetPassword(id, newPassword);
        return Result.success("重置成功", result);
    }

    @Operation(summary = "批量删除用户")
    @DeleteMapping("/batch")
    @SysLog(module = "用户管理", operation = "批量删除用户")
    public Result<Boolean> batchDelete(@RequestBody List<Long> ids) {
        boolean result = sysUserService.removeByIds(ids);
        return Result.success("删除成功", result);
    }
}

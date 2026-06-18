package com.safetycampus.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.safetycampus.common.annotation.SysLog;
import com.safetycampus.common.result.Result;
import com.safetycampus.system.dto.SysParamDTO;
import com.safetycampus.system.entity.SysParam;
import com.safetycampus.system.service.SysParamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "系统参数接口", description = "系统参数增删改查、缓存刷新等功能")
@RestController
@RequestMapping("/api/system/param")
public class SysParamController {

    @Resource
    private SysParamService sysParamService;

    @Operation(summary = "分页查询系统参数")
    @GetMapping("/page")
    public Result<IPage<SysParam>> page(
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<SysParam> page = sysParamService.selectPage(keyword, pageNum, pageSize);
        return Result.success(page);
    }

    @Operation(summary = "获取参数详情")
    @GetMapping("/{id}")
    public Result<SysParam> getDetail(@Parameter(description = "参数ID") @PathVariable Long id) {
        SysParam param = sysParamService.getById(id);
        return Result.success(param);
    }

    @Operation(summary = "根据参数键获取参数值")
    @GetMapping("/key/{paramKey}")
    public Result<String> getValueByKey(@Parameter(description = "参数键") @PathVariable String paramKey) {
        String value = sysParamService.getValueByKey(paramKey);
        return Result.success(value);
    }

    @Operation(summary = "新增系统参数")
    @PostMapping
    @SysLog(module = "系统参数", operation = "新增系统参数")
    public Result<Boolean> add(@Valid @RequestBody SysParamDTO dto) {
        boolean result = sysParamService.saveParam(dto);
        return Result.success("新增成功", result);
    }

    @Operation(summary = "修改系统参数")
    @PutMapping
    @SysLog(module = "系统参数", operation = "修改系统参数")
    public Result<Boolean> update(@Valid @RequestBody SysParamDTO dto) {
        boolean result = sysParamService.updateParam(dto);
        return Result.success("修改成功", result);
    }

    @Operation(summary = "删除系统参数")
    @DeleteMapping("/{id}")
    @SysLog(module = "系统参数", operation = "删除系统参数")
    public Result<Boolean> delete(@Parameter(description = "参数ID") @PathVariable Long id) {
        boolean result = sysParamService.deleteParam(id);
        return Result.success("删除成功", result);
    }

    @Operation(summary = "刷新参数缓存")
    @PostMapping("/refresh-cache")
    @SysLog(module = "系统参数", operation = "刷新参数缓存")
    public Result<Boolean> refreshCache() {
        boolean result = sysParamService.refreshCache();
        return Result.success("刷新成功", result);
    }
}

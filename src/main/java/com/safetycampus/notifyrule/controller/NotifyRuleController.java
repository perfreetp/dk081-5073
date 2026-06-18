package com.safetycampus.notifyrule.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.safetycampus.common.result.Result;
import com.safetycampus.notifyrule.dto.NotifyRuleDTO;
import com.safetycampus.notifyrule.dto.NotifyRuleQueryDTO;
import com.safetycampus.notifyrule.dto.NotifyRuleTestDTO;
import com.safetycampus.notifyrule.dto.NotifyRuleTestResultVO;
import com.safetycampus.notifyrule.entity.NotifyRule;
import com.safetycampus.notifyrule.service.NotifyRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notify/rule")
@Tag(name = "通知规则管理", description = "通知规则的CRUD操作和规则试算")
public class NotifyRuleController {

    @Autowired
    private NotifyRuleService notifyRuleService;

    @PostMapping
    @Operation(summary = "创建通知规则")
    public Result<Void> create(@Valid @RequestBody NotifyRuleDTO dto) {
        notifyRuleService.createRule(dto);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "更新通知规则")
    public Result<Void> update(@Valid @RequestBody NotifyRuleDTO dto) {
        notifyRuleService.updateRule(dto);
        return Result.success();
    }

    @PutMapping("/{id}/toggle")
    @Operation(summary = "启用/停用规则")
    public Result<Void> toggle(
            @Parameter(description = "规则ID") @PathVariable Long id,
            @Parameter(description = "是否启用") @RequestParam Boolean enabled) {
        notifyRuleService.toggleRule(id, enabled);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除规则")
    public Result<Void> delete(@Parameter(description = "规则ID") @PathVariable Long id) {
        notifyRuleService.deleteRule(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取规则详情")
    public Result<NotifyRule> getById(@Parameter(description = "规则ID") @PathVariable Long id) {
        return Result.success(notifyRuleService.getRuleDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询规则列表")
    public Result<IPage<NotifyRule>> page(NotifyRuleQueryDTO queryDTO) {
        return Result.success(notifyRuleService.selectPage(queryDTO));
    }

    @PostMapping("/test")
    @Operation(summary = "规则试算")
    public Result<NotifyRuleTestResultVO> test(@Valid @RequestBody NotifyRuleTestDTO dto) {
        return Result.success(notifyRuleService.testMatchRule(dto));
    }
}

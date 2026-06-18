package com.safetycampus.plan.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.safetycampus.common.result.Result;
import com.safetycampus.plan.dto.DisposalPlanDTO;
import com.safetycampus.plan.dto.DisposalPlanQueryDTO;
import com.safetycampus.plan.entity.DisposalPlan;
import com.safetycampus.plan.entity.DisposalPlanStep;
import com.safetycampus.plan.service.DisposalPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plan")
@Tag(name = "处置预案管理", description = "处置预案的配置管理")
public class DisposalPlanController {

    @Autowired
    private DisposalPlanService disposalPlanService;

    @PostMapping
    @Operation(summary = "创建处置预案")
    public Result<Void> create(@Valid @RequestBody DisposalPlanDTO dto) {
        disposalPlanService.createPlan(dto);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "更新处置预案")
    public Result<Void> update(@Valid @RequestBody DisposalPlanDTO dto) {
        disposalPlanService.updatePlan(dto);
        return Result.success();
    }

    @PutMapping("/{id}/toggle")
    @Operation(summary = "启用/停用预案")
    public Result<Void> toggle(
            @Parameter(description = "预案ID") @PathVariable Long id,
            @Parameter(description = "是否启用") @RequestParam Boolean enabled) {
        disposalPlanService.togglePlan(id, enabled);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除预案")
    public Result<Void> delete(@Parameter(description = "预案ID") @PathVariable Long id) {
        disposalPlanService.deletePlan(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取预案详情")
    public Result<DisposalPlan> getById(@Parameter(description = "预案ID") @PathVariable Long id) {
        return Result.success(disposalPlanService.getPlanDetail(id));
    }

    @GetMapping("/{id}/steps")
    @Operation(summary = "获取预案步骤列表")
    public Result<List<DisposalPlanStep>> getSteps(@Parameter(description = "预案ID") @PathVariable Long id) {
        return Result.success(disposalPlanService.getPlanSteps(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询预案列表")
    public Result<IPage<DisposalPlan>> page(DisposalPlanQueryDTO queryDTO) {
        return Result.success(disposalPlanService.selectPage(queryDTO));
    }
}

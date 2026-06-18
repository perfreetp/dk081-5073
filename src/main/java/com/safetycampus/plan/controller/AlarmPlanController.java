package com.safetycampus.plan.controller;

import com.safetycampus.common.result.Result;
import com.safetycampus.plan.dto.PlanStepExecDTO;
import com.safetycampus.plan.service.AlarmPlanService;
import com.safetycampus.plan.vo.AlarmPlanDetailVO;
import com.safetycampus.plan.vo.PlanMatchResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plan")
@Tag(name = "警情预案联动", description = "警情与处置预案的关联和执行")
public class AlarmPlanController {

    @Autowired
    private AlarmPlanService alarmPlanService;

    @PostMapping("/alarm/match/{alarmId}")
    @Operation(summary = "手动触发匹配预案")
    public Result<PlanMatchResultVO> match(
            @Parameter(description = "警情ID") @PathVariable Long alarmId) {
        return Result.success(alarmPlanService.matchAndLinkPlan(alarmId));
    }

    @GetMapping("/alarm/{alarmId}")
    @Operation(summary = "获取警情关联预案详情")
    public Result<AlarmPlanDetailVO> getAlarmPlanDetail(
            @Parameter(description = "警情ID") @PathVariable Long alarmId) {
        return Result.success(alarmPlanService.getAlarmPlanDetail(alarmId));
    }

    @PutMapping("/link/{linkId}/start")
    @Operation(summary = "启动预案执行")
    public Result<Void> startExecution(
            @Parameter(description = "关联ID") @PathVariable Long linkId) {
        alarmPlanService.startPlanExecution(linkId);
        return Result.success();
    }

    @PutMapping("/step/execute")
    @Operation(summary = "执行回填步骤")
    public Result<Void> executeStep(@Valid @RequestBody PlanStepExecDTO dto) {
        alarmPlanService.executeStep(dto);
        return Result.success();
    }

    @PutMapping("/step/skip")
    @Operation(summary = "跳过步骤")
    public Result<Void> skipStep(@Valid @RequestBody PlanStepExecDTO dto) {
        alarmPlanService.skipStep(dto);
        return Result.success();
    }

    @PutMapping("/link/{linkId}/complete")
    @Operation(summary = "完成预案")
    public Result<Void> completePlan(
            @Parameter(description = "关联ID") @PathVariable Long linkId,
            @Parameter(description = "总结") @RequestParam(required = false) String summary) {
        alarmPlanService.completePlan(linkId, summary);
        return Result.success();
    }

    @PutMapping("/alarm/{alarmId}/close")
    @Operation(summary = "关闭警情时回填预案")
    public Result<Void> closeAlarm(
            @Parameter(description = "警情ID") @PathVariable Long alarmId,
            @Parameter(description = "关联ID") @RequestParam(required = false) Long linkId,
            @Parameter(description = "总结") @RequestParam(required = false) String summary) {
        alarmPlanService.closeAlarmWithPlan(alarmId, linkId, summary);
        return Result.success();
    }
}

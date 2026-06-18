package com.safetycampus.alarm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.safetycampus.alarm.dto.AlarmMergeDTO;
import com.safetycampus.alarm.dto.AlarmQueryDTO;
import com.safetycampus.alarm.entity.AlarmFlow;
import com.safetycampus.alarm.entity.AlarmRecord;
import com.safetycampus.alarm.service.AlarmFlowService;
import com.safetycampus.alarm.service.AlarmRecordService;
import com.safetycampus.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "警情管理接口", description = "警情查询、处置、合并、上推等管理功能")
@RestController
@RequestMapping("/api/alarm/record")
public class AlarmRecordController {

    @Resource
    private AlarmRecordService alarmRecordService;

    @Resource
    private AlarmFlowService alarmFlowService;

    @Operation(summary = "分页查询警情列表")
    @GetMapping("/page")
    public Result<IPage<AlarmRecord>> page(AlarmQueryDTO queryDTO) {
        IPage<AlarmRecord> page = alarmRecordService.selectPage(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "获取警情详情")
    @GetMapping("/{id}")
    public Result<AlarmRecord> getDetail(@Parameter(description = "警情ID") @PathVariable Long id) {
        AlarmRecord record = alarmRecordService.getDetail(id);
        return Result.success(record);
    }

    @Operation(summary = "获取警情流转记录")
    @GetMapping("/{id}/flow")
    public Result<List<AlarmFlow>> getFlow(@Parameter(description = "警情ID") @PathVariable Long id) {
        List<AlarmFlow> flowList = alarmFlowService.getFlowByAlarmId(id);
        return Result.success(flowList);
    }

    @Operation(summary = "合并重复报警")
    @PostMapping("/merge")
    public Result<Boolean> mergeAlarms(@Valid @RequestBody AlarmMergeDTO mergeDTO) {
        boolean result = alarmRecordService.mergeDuplicateAlarms(mergeDTO);
        return Result.success("合并成功", result);
    }

    @Operation(summary = "重大警情上推")
    @PostMapping("/{id}/escalate")
    public Result<Boolean> escalateAlarm(
            @Parameter(description = "警情ID") @PathVariable Long id,
            @Parameter(description = "操作人ID") @RequestParam(required = false) Long operatorId,
            @Parameter(description = "操作人姓名") @RequestParam String operatorName) {
        boolean result = alarmRecordService.escalateCriticalAlarm(id, operatorId, operatorName);
        return Result.success("上推成功", result);
    }
}

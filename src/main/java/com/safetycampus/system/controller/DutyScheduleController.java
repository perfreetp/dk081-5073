package com.safetycampus.system.controller;

import com.safetycampus.common.annotation.SysLog;
import com.safetycampus.common.result.Result;
import com.safetycampus.system.dto.DutyScheduleDTO;
import com.safetycampus.system.entity.DutySchedule;
import com.safetycampus.system.service.DutyScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "值班排班接口", description = "值班排班增删改查、批量排班等功能")
@RestController
@RequestMapping("/api/system/duty")
public class DutyScheduleController {

    @Resource
    private DutyScheduleService dutyScheduleService;

    @Operation(summary = "按日期范围查询排班")
    @GetMapping("/list")
    public Result<List<DutySchedule>> list(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<DutySchedule> list = dutyScheduleService.getByDateRange(startDate, endDate);
        return Result.success(list);
    }

    @Operation(summary = "按用户和日期范围查询排班")
    @GetMapping("/user/{userId}")
    public Result<List<DutySchedule>> listByUser(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<DutySchedule> list = dutyScheduleService.getByUserIdAndDateRange(userId, startDate, endDate);
        return Result.success(list);
    }

    @Operation(summary = "获取排班详情")
    @GetMapping("/{id}")
    public Result<DutySchedule> getDetail(@Parameter(description = "排班ID") @PathVariable Long id) {
        DutySchedule schedule = dutyScheduleService.getDetail(id);
        return Result.success(schedule);
    }

    @Operation(summary = "新增排班")
    @PostMapping
    @SysLog(module = "值班排班", operation = "新增排班")
    public Result<Boolean> add(@Valid @RequestBody DutyScheduleDTO dto) {
        boolean result = dutyScheduleService.saveSchedule(dto);
        return Result.success("新增成功", result);
    }

    @Operation(summary = "修改排班")
    @PutMapping
    @SysLog(module = "值班排班", operation = "修改排班")
    public Result<Boolean> update(@Valid @RequestBody DutyScheduleDTO dto) {
        boolean result = dutyScheduleService.updateSchedule(dto);
        return Result.success("修改成功", result);
    }

    @Operation(summary = "删除排班")
    @DeleteMapping("/{id}")
    @SysLog(module = "值班排班", operation = "删除排班")
    public Result<Boolean> delete(@Parameter(description = "排班ID") @PathVariable Long id) {
        boolean result = dutyScheduleService.deleteSchedule(id);
        return Result.success("删除成功", result);
    }

    @Operation(summary = "批量新增排班")
    @PostMapping("/batch")
    @SysLog(module = "值班排班", operation = "批量新增排班")
    public Result<Boolean> batchAdd(@Valid @RequestBody List<DutyScheduleDTO> dtoList) {
        boolean result = dutyScheduleService.batchSave(dtoList);
        return Result.success("批量新增成功", result);
    }
}

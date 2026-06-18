package com.safetycampus.command.controller;

import com.safetycampus.command.dto.CommandNoteDTO;
import com.safetycampus.command.dto.CommandRemindDTO;
import com.safetycampus.command.dto.CommandTransferDTO;
import com.safetycampus.command.service.CommandService;
import com.safetycampus.command.vo.CommandDashboardVO;
import com.safetycampus.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "值守指挥研判台接口", description = "研判台主视图、警情信息、学校详情、派出所、通知链路、督办进度、补充说明、催办转派等功能")
@RestController
@RequestMapping("/api/command")
public class CommandController {

    @Resource
    private CommandService commandService;

    @Operation(summary = "获取研判台主视图")
    @GetMapping("/dashboard/{alarmId}")
    public Result<CommandDashboardVO> getCommandDashboard(
            @Parameter(description = "警情ID", required = true) @PathVariable Long alarmId) {
        CommandDashboardVO vo = commandService.getCommandDashboard(alarmId);
        return Result.success(vo);
    }

    @Operation(summary = "获取警情简要信息")
    @GetMapping("/alarm/{id}/brief")
    public Result<CommandDashboardVO.AlarmBriefVO> getAlarmBrief(
            @Parameter(description = "警情ID", required = true) @PathVariable("id") Long alarmId) {
        CommandDashboardVO.AlarmBriefVO vo = commandService.getAlarmBrief(alarmId);
        return Result.success(vo);
    }

    @Operation(summary = "获取学校详情")
    @GetMapping("/school/{id}/detail")
    public Result<CommandDashboardVO.SchoolDetailVO> getSchoolDetail(
            @Parameter(description = "学校ID", required = true) @PathVariable("id") Long schoolId) {
        CommandDashboardVO.SchoolDetailVO vo = commandService.getSchoolDetail(schoolId);
        return Result.success(vo);
    }

    @Operation(summary = "获取附近派出所信息")
    @GetMapping("/police/nearby/{schoolId}")
    public Result<CommandDashboardVO.PoliceStationVO> getNearbyPoliceStation(
            @Parameter(description = "学校ID", required = true) @PathVariable Long schoolId) {
        CommandDashboardVO.PoliceStationVO vo = commandService.getNearbyPoliceStation(schoolId);
        return Result.success(vo);
    }

    @Operation(summary = "获取最近报警历史")
    @GetMapping("/school/{id}/history")
    public Result<List<CommandDashboardVO.AlarmHistoryVO>> getAlarmHistory(
            @Parameter(description = "学校ID", required = true) @PathVariable("id") Long schoolId,
            @Parameter(description = "返回数量，默认10") @RequestParam(defaultValue = "10") int limit) {
        List<CommandDashboardVO.AlarmHistoryVO> list = commandService.getAlarmHistory(schoolId, limit);
        return Result.success(list);
    }

    @Operation(summary = "获取学校风险画像")
    @GetMapping("/school/{id}/risk")
    public Result<CommandDashboardVO.RiskPortraitVO> getRiskPortrait(
            @Parameter(description = "学校ID", required = true) @PathVariable("id") Long schoolId) {
        CommandDashboardVO.RiskPortraitVO vo = commandService.getRiskPortrait(schoolId);
        return Result.success(vo);
    }

    @Operation(summary = "获取通知链路")
    @GetMapping("/alarm/{id}/notify-link")
    public Result<CommandDashboardVO.NotifyLinkVO> getNotifyLink(
            @Parameter(description = "警情ID", required = true) @PathVariable("id") Long alarmId) {
        CommandDashboardVO.NotifyLinkVO vo = commandService.getNotifyLink(alarmId);
        return Result.success(vo);
    }

    @Operation(summary = "获取督办进度")
    @GetMapping("/alarm/{id}/supervise-progress")
    public Result<CommandDashboardVO.SuperviseProgressVO> getSuperviseProgress(
            @Parameter(description = "警情ID", required = true) @PathVariable("id") Long alarmId) {
        CommandDashboardVO.SuperviseProgressVO vo = commandService.getSuperviseProgress(alarmId);
        return Result.success(vo);
    }

    @Operation(summary = "获取补充说明列表")
    @GetMapping("/alarm/{id}/notes")
    public Result<List<CommandDashboardVO.SupplementNoteVO>> getSupplementNotes(
            @Parameter(description = "警情ID", required = true) @PathVariable("id") Long alarmId) {
        List<CommandDashboardVO.SupplementNoteVO> list = commandService.getSupplementNotes(alarmId);
        return Result.success(list);
    }

    @Operation(summary = "一键催办")
    @PostMapping("/alarm/remind")
    public Result<Boolean> remindAlarm(@Valid @RequestBody CommandRemindDTO dto) {
        boolean result = commandService.remindAlarm(dto);
        return Result.success(result);
    }

    @Operation(summary = "转派警情")
    @PostMapping("/alarm/transfer")
    public Result<Boolean> transferAlarm(@Valid @RequestBody CommandTransferDTO dto) {
        boolean result = commandService.transferAlarm(dto);
        return Result.success(result);
    }

    @Operation(summary = "新增补充说明")
    @PostMapping("/alarm/note")
    public Result<Boolean> addNote(@Valid @RequestBody CommandNoteDTO dto) {
        boolean result = commandService.addNote(dto);
        return Result.success(result);
    }
}

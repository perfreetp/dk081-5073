package com.safetycampus.notify.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.safetycampus.common.entity.PageQuery;
import com.safetycampus.common.result.Result;
import com.safetycampus.notify.dto.NotifySendDTO;
import com.safetycampus.notify.entity.NotifyRecord;
import com.safetycampus.notify.service.NotifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "通知管理接口", description = "短信、APP推送、电话通知的发送和记录查询")
@RestController
@RequestMapping("/api/notify")
public class NotifyController {

    @Resource
    private NotifyService notifyService;

    @Operation(summary = "发送短信")
    @PostMapping("/sms")
    public Result<Boolean> sendSms(
            @Parameter(description = "手机号") @RequestParam String phone,
            @Parameter(description = "接收人姓名") @RequestParam(required = false) String name,
            @Parameter(description = "标题") @RequestParam String title,
            @Parameter(description = "内容") @RequestParam String content,
            @Parameter(description = "模板编码") @RequestParam(required = false) String templateCode,
            @Parameter(description = "关联警情ID") @RequestParam(required = false) Long alarmId) {
        boolean result = notifyService.sendSms(phone, name, title, content, templateCode, alarmId);
        return Result.success(result ? "发送成功" : "发送失败", result);
    }

    @Operation(summary = "APP推送")
    @PostMapping("/app-push")
    public Result<Boolean> sendAppPush(
            @Parameter(description = "目标设备/用户ID") @RequestParam String target,
            @Parameter(description = "接收人姓名") @RequestParam(required = false) String name,
            @Parameter(description = "标题") @RequestParam String title,
            @Parameter(description = "内容") @RequestParam String content,
            @Parameter(description = "关联警情ID") @RequestParam(required = false) Long alarmId) {
        boolean result = notifyService.sendAppPush(target, name, title, content, alarmId);
        return Result.success(result ? "推送成功" : "推送失败", result);
    }

    @Operation(summary = "电话通知")
    @PostMapping("/call")
    public Result<Boolean> sendCall(
            @Parameter(description = "手机号") @RequestParam String phone,
            @Parameter(description = "接收人姓名") @RequestParam(required = false) String name,
            @Parameter(description = "标题") @RequestParam String title,
            @Parameter(description = "内容") @RequestParam String content,
            @Parameter(description = "关联警情ID") @RequestParam(required = false) Long alarmId) {
        boolean result = notifyService.sendCall(phone, name, title, content, alarmId);
        return Result.success(result ? "呼叫成功" : "呼叫失败", result);
    }

    @Operation(summary = "批量通知")
    @PostMapping("/batch")
    public Result<Boolean> batchNotify(@Valid @RequestBody NotifySendDTO dto) {
        boolean result = notifyService.batchNotify(dto);
        return Result.success(result ? "批量通知完成" : "批量通知失败", result);
    }

    @Operation(summary = "通知属地派出所")
    @PostMapping("/police-station/{policeStationId}")
    public Result<Boolean> notifyPoliceStation(
            @Parameter(description = "派出所ID") @PathVariable Long policeStationId,
            @Parameter(description = "关联警情ID") @RequestParam Long alarmId,
            @Parameter(description = "警情标题") @RequestParam String alarmTitle,
            @Parameter(description = "警情内容") @RequestParam String alarmContent) {
        boolean result = notifyService.notifyPoliceStation(policeStationId, alarmId, alarmTitle, alarmContent);
        return Result.success(result ? "通知成功" : "通知失败", result);
    }

    @Operation(summary = "分页查询通知记录")
    @GetMapping("/record/page")
    public Result<IPage<NotifyRecord>> page(
            PageQuery query,
            @Parameter(description = "关联警情ID") @RequestParam(required = false) Long alarmId,
            @Parameter(description = "通知类型") @RequestParam(required = false) Integer notifyType,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        IPage<NotifyRecord> page = notifyService.selectPage(query, alarmId, notifyType, status);
        return Result.success(page);
    }

    @Operation(summary = "查询警情的通知记录")
    @GetMapping("/record/alarm/{alarmId}")
    public Result<List<NotifyRecord>> listByAlarmId(@Parameter(description = "警情ID") @PathVariable Long alarmId) {
        List<NotifyRecord> list = notifyService.listByAlarmId(alarmId);
        return Result.success(list);
    }

    @Operation(summary = "标记为已读")
    @PutMapping("/record/{id}/read")
    public Result<Boolean> markAsRead(@Parameter(description = "通知记录ID") @PathVariable Long id) {
        boolean result = notifyService.markAsRead(id);
        return Result.success("标记成功", result);
    }

    @Operation(summary = "重发通知")
    @PostMapping("/record/{id}/resend")
    public Result<Boolean> resend(@Parameter(description = "通知记录ID") @PathVariable Long id) {
        boolean result = notifyService.resend(id);
        return Result.success(result ? "重发成功" : "重发失败", result);
    }
}

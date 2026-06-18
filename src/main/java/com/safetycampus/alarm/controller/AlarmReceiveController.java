package com.safetycampus.alarm.controller;

import com.safetycampus.alarm.dto.AlarmReceiveDTO;
import com.safetycampus.alarm.entity.AlarmRecord;
import com.safetycampus.alarm.service.AlarmRecordService;
import com.safetycampus.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "报警接入接口", description = "供校内终端调用的报警上报接口")
@RestController
@RequestMapping("/api/alarm/receive")
public class AlarmReceiveController {

    @Resource
    private AlarmRecordService alarmRecordService;

    @Operation(summary = "接收报警", description = "校内终端上报警情")
    @PostMapping
    public Result<AlarmRecord> receiveAlarm(@Valid @RequestBody AlarmReceiveDTO dto) {
        AlarmRecord record = alarmRecordService.receiveAlarm(dto);
        return Result.success("报警接收成功", record);
    }

    @Operation(summary = "检测节假日模式")
    @PostMapping("/check-holiday")
    public Result<Boolean> checkHolidayMode() {
        boolean isHoliday = alarmRecordService.checkHolidayMode();
        return Result.success(isHoliday);
    }
}

package com.safetycampus.school.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.safetycampus.common.result.Result;
import com.safetycampus.school.dto.DeviceBindDTO;
import com.safetycampus.school.entity.AlarmDevice;
import com.safetycampus.school.service.AlarmDeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/school/device")
@Tag(name = "报警设备管理", description = "报警设备的绑定、解绑、状态更新等操作")
public class AlarmDeviceController {

    @Autowired
    private AlarmDeviceService alarmDeviceService;

    @GetMapping("/page")
    @Operation(summary = "分页查询设备列表")
    public Result<IPage<AlarmDevice>> page(
            @RequestParam(required = false) Long schoolId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(alarmDeviceService.selectPage(schoolId, pageNum, pageSize));
    }

    @GetMapping("/list/{schoolId}")
    @Operation(summary = "获取学校下的所有设备")
    public Result<List<AlarmDevice>> listBySchoolId(@PathVariable Long schoolId) {
        return Result.success(alarmDeviceService.listBySchoolId(schoolId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取设备详情")
    public Result<AlarmDevice> getById(@PathVariable Long id) {
        return Result.success(alarmDeviceService.getById(id));
    }

    @PostMapping
    @Operation(summary = "绑定设备")
    public Result<Void> bind(@Valid @RequestBody DeviceBindDTO dto) {
        alarmDeviceService.bindDevice(dto);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "更新设备信息")
    public Result<Void> update(@Valid @RequestBody DeviceBindDTO dto) {
        alarmDeviceService.updateDevice(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "解绑设备")
    public Result<Void> unbind(@PathVariable Long id) {
        alarmDeviceService.unbindDevice(id);
        return Result.success();
    }

    @PutMapping("/status")
    @Operation(summary = "更新设备状态")
    public Result<Void> updateStatus(
            @RequestParam String deviceCode,
            @RequestParam Integer status) {
        alarmDeviceService.updateDeviceStatus(deviceCode, status);
        return Result.success();
    }
}

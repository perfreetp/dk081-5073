package com.safetycampus.notify.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.safetycampus.common.result.Result;
import com.safetycampus.notify.dto.PoliceStationDTO;
import com.safetycampus.notify.entity.PoliceStation;
import com.safetycampus.notify.service.PoliceStationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "派出所管理接口", description = "派出所信息的增删改查管理")
@RestController
@RequestMapping("/api/notify/police-station")
public class PoliceStationController {

    @Resource
    private PoliceStationService policeStationService;

    @Operation(summary = "分页查询派出所列表")
    @GetMapping("/page")
    public Result<IPage<PoliceStation>> page(PoliceStationDTO dto) {
        IPage<PoliceStation> page = policeStationService.selectPage(dto);
        return Result.success(page);
    }

    @Operation(summary = "获取所有派出所列表")
    @GetMapping("/list")
    public Result<List<PoliceStation>> list() {
        List<PoliceStation> list = policeStationService.listAll();
        return Result.success(list);
    }

    @Operation(summary = "获取派出所详情")
    @GetMapping("/{id}")
    public Result<PoliceStation> getDetail(@Parameter(description = "派出所ID") @PathVariable Long id) {
        PoliceStation station = policeStationService.getDetail(id);
        return Result.success(station);
    }

    @Operation(summary = "新增派出所")
    @PostMapping
    public Result<Boolean> add(@Valid @RequestBody PoliceStationDTO dto) {
        boolean result = policeStationService.add(dto);
        return Result.success("新增成功", result);
    }

    @Operation(summary = "修改派出所")
    @PutMapping
    public Result<Boolean> update(@Valid @RequestBody PoliceStationDTO dto) {
        boolean result = policeStationService.update(dto);
        return Result.success("修改成功", result);
    }

    @Operation(summary = "删除派出所")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@Parameter(description = "派出所ID") @PathVariable Long id) {
        boolean result = policeStationService.delete(id);
        return Result.success("删除成功", result);
    }
}

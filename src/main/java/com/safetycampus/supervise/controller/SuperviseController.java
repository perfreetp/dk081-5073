package com.safetycampus.supervise.controller;

import com.safetycampus.common.result.Result;
import com.safetycampus.supervise.dto.AlarmTransferDTO;
import com.safetycampus.supervise.dto.HandleFeedbackDTO;
import com.safetycampus.supervise.dto.SuperviseCreateDTO;
import com.safetycampus.supervise.service.SuperviseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "督办管理接口", description = "警情督办、处置反馈、转派、关闭、催办等功能")
@RestController
@RequestMapping("/api/supervise")
public class SuperviseController {

    @Resource
    private SuperviseService superviseService;

    @Operation(summary = "创建督办")
    @PostMapping
    public Result<Boolean> createSupervise(@Valid @RequestBody SuperviseCreateDTO dto) {
        boolean result = superviseService.createSupervise(dto);
        return Result.success("督办创建成功", result);
    }

    @Operation(summary = "处置反馈")
    @PutMapping("/feedback")
    public Result<Boolean> handleFeedback(@Valid @RequestBody HandleFeedbackDTO dto) {
        boolean result = superviseService.handleFeedback(dto);
        return Result.success("反馈提交成功", result);
    }

    @Operation(summary = "转派警情")
    @PutMapping("/transfer")
    public Result<Boolean> transferAlarm(@Valid @RequestBody AlarmTransferDTO dto) {
        boolean result = superviseService.transferAlarm(dto);
        return Result.success("转派成功", result);
    }

    @Operation(summary = "关闭警情")
    @PutMapping("/close/{id}")
    public Result<Boolean> closeAlarm(
            @Parameter(description = "警情ID") @PathVariable Long id,
            @Parameter(description = "关闭备注") @RequestParam(required = false) String remark) {
        boolean result = superviseService.closeAlarm(id, remark);
        return Result.success("关闭成功", result);
    }

    @Operation(summary = "手动催办")
    @PostMapping("/remind/{id}")
    public Result<Boolean> manualRemind(@Parameter(description = "警情ID") @PathVariable Long id) {
        boolean result = superviseService.manualRemind(id);
        return Result.success("催办成功", result);
    }
}

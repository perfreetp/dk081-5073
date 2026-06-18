package com.safetycampus.report.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.safetycampus.common.result.Result;
import com.safetycampus.report.dto.AssessQueryDTO;
import com.safetycampus.report.entity.AssessRecord;
import com.safetycampus.report.service.AssessRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "考核排名接口", description = "季度考核计算、排名生成、排名查询等功能")
@RestController
@RequestMapping("/api/report/assess")
public class AssessRecordController {

    @Resource
    private AssessRecordService assessRecordService;

    @Operation(summary = "分页查询考核记录列表")
    @GetMapping("/page")
    public Result<IPage<AssessRecord>> page(AssessQueryDTO queryDTO) {
        IPage<AssessRecord> page = assessRecordService.selectPage(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "获取考核记录详情")
    @GetMapping("/{id}")
    public Result<AssessRecord> getDetail(@Parameter(description = "考核记录ID") @PathVariable Long id) {
        AssessRecord record = assessRecordService.getDetail(id);
        return Result.success(record);
    }

    @Operation(summary = "计算季度考核")
    @PostMapping("/calculate")
    public Result<AssessRecord> calculateQuarterAssess(
            @Parameter(description = "学校ID") @RequestParam Long schoolId,
            @Parameter(description = "统计季度(YYYY-QN)") @RequestParam String statQuarter) {
        AssessRecord record = assessRecordService.calculateQuarterAssess(schoolId, statQuarter);
        return Result.success("计算成功", record);
    }

    @Operation(summary = "生成季度排名")
    @PostMapping("/rank/generate")
    public Result<Boolean> generateRank(
            @Parameter(description = "统计季度(YYYY-QN)") @RequestParam String statQuarter) {
        boolean result = assessRecordService.generateRank(statQuarter);
        return Result.success("排名生成成功", result);
    }

    @Operation(summary = "查询季度排名")
    @GetMapping("/rank")
    public Result<List<AssessRecord>> getQuarterRank(
            @Parameter(description = "统计季度(YYYY-QN)") @RequestParam String statQuarter) {
        List<AssessRecord> list = assessRecordService.getQuarterRank(statQuarter);
        return Result.success(list);
    }
}

package com.safetycampus.report.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.safetycampus.common.result.Result;
import com.safetycampus.report.dto.RiskQueryDTO;
import com.safetycampus.report.entity.SchoolRisk;
import com.safetycampus.report.service.SchoolRiskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "学校风险画像接口", description = "学校风险评分、画像生成、高风险学校查询等功能")
@RestController
@RequestMapping("/api/report/risk")
public class SchoolRiskController {

    @Resource
    private SchoolRiskService schoolRiskService;

    @Operation(summary = "分页查询风险画像列表")
    @GetMapping("/page")
    public Result<IPage<SchoolRisk>> page(RiskQueryDTO queryDTO) {
        IPage<SchoolRisk> page = schoolRiskService.selectPage(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "获取风险画像详情")
    @GetMapping("/{id}")
    public Result<SchoolRisk> getDetail(@Parameter(description = "风险画像ID") @PathVariable Long id) {
        SchoolRisk risk = schoolRiskService.getDetail(id);
        return Result.success(risk);
    }

    @Operation(summary = "计算风险评分")
    @PostMapping("/calculate")
    public Result<BigDecimal> calculateRiskScore(
            @Parameter(description = "学校ID") @RequestParam Long schoolId,
            @Parameter(description = "统计月份(YYYY-MM)") @RequestParam String statMonth) {
        BigDecimal score = schoolRiskService.calculateRiskScore(schoolId, statMonth);
        return Result.success("计算成功", score);
    }

    @Operation(summary = "生成风险画像")
    @PostMapping("/generate")
    public Result<SchoolRisk> generateRiskPortrait(
            @Parameter(description = "学校ID") @RequestParam Long schoolId,
            @Parameter(description = "统计月份(YYYY-MM)") @RequestParam String statMonth) {
        SchoolRisk risk = schoolRiskService.generateRiskPortrait(schoolId, statMonth);
        return Result.success("生成成功", risk);
    }

    @Operation(summary = "获取高风险学校列表")
    @GetMapping("/high-risk")
    public Result<List<SchoolRisk>> getHighRiskSchools(
            @Parameter(description = "统计月份(YYYY-MM)") @RequestParam(required = false) String statMonth) {
        List<SchoolRisk> list = schoolRiskService.getHighRiskSchools(statMonth);
        return Result.success(list);
    }
}

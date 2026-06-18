package com.safetycampus.report.controller;

import com.safetycampus.common.result.Result;
import com.safetycampus.report.dto.ReportStatisticsDTO;
import com.safetycampus.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Tag(name = "统计报表接口", description = "警情统计、处置统计、趋势分析、报表导出等功能")
@RestController
@RequestMapping("/api/report/statistics")
public class ReportController {

    @Resource
    private ReportService reportService;

    @Operation(summary = "警情统计报表")
    @GetMapping("/alarm")
    public Result<ReportStatisticsDTO> getAlarmStatistics(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "学校ID") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "分组ID") @RequestParam(required = false) Long groupId) {
        ReportStatisticsDTO dto = reportService.getAlarmStatistics(startDate, endDate, schoolId, groupId);
        return Result.success(dto);
    }

    @Operation(summary = "处置统计报表")
    @GetMapping("/handle")
    public Result<ReportStatisticsDTO> getHandleStatistics(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "学校ID") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "分组ID") @RequestParam(required = false) Long groupId) {
        ReportStatisticsDTO dto = reportService.getHandleStatistics(startDate, endDate, schoolId, groupId);
        return Result.success(dto);
    }

    @Operation(summary = "趋势分析报表")
    @GetMapping("/trend")
    public Result<ReportStatisticsDTO> getTrendStatistics(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "学校ID") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "分组ID") @RequestParam(required = false) Long groupId) {
        ReportStatisticsDTO dto = reportService.getTrendStatistics(startDate, endDate, schoolId, groupId);
        return Result.success(dto);
    }

    @Operation(summary = "导出报表(Excel)")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportReport(
            @Parameter(description = "报表类型:alarm-警情统计,handle-处置统计,trend-趋势分析") @RequestParam String reportType,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "学校ID") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "分组ID") @RequestParam(required = false) Long groupId) {
        byte[] data = reportService.exportReport(reportType, startDate, endDate, schoolId, groupId);
        
        String fileName = getReportFileName(reportType, startDate, endDate);
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", encodedFileName);
        headers.setContentLength(data.length);
        
        return ResponseEntity.ok().headers(headers).body(data);
    }

    private String getReportFileName(String reportType, LocalDate startDate, LocalDate endDate) {
        String typeName = switch (reportType) {
            case "alarm" -> "警情统计报表";
            case "handle" -> "处置统计报表";
            case "trend" -> "趋势分析报表";
            default -> "统计报表";
        };
        return typeName + "_" + startDate + "_" + endDate + ".xlsx";
    }
}

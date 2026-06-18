package com.safetycampus.report.controller;

import com.safetycampus.common.result.Result;
import com.safetycampus.report.dto.CompareQueryDTO;
import com.safetycampus.report.dto.DutyReviewQueryDTO;
import com.safetycampus.report.dto.ReportStatisticsDTO;
import com.safetycampus.report.service.ReportService;
import com.safetycampus.report.vo.CompareResultVO;
import com.safetycampus.report.vo.DutyReviewSchoolVO;
import com.safetycampus.report.vo.DutyReviewShiftVO;
import com.safetycampus.report.vo.DutyReviewSummaryVO;
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
import java.util.List;

@Tag(name = "统计报表接口", description = "警情统计、处置统计、趋势分析、报表导出等功能")
@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Resource
    private ReportService reportService;

    @Operation(summary = "警情统计报表")
    @GetMapping("/statistics/alarm")
    public Result<ReportStatisticsDTO> getAlarmStatistics(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "学校ID") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "分组ID") @RequestParam(required = false) Long groupId) {
        ReportStatisticsDTO dto = reportService.getAlarmStatistics(startDate, endDate, schoolId, groupId);
        return Result.success(dto);
    }

    @Operation(summary = "处置统计报表")
    @GetMapping("/statistics/handle")
    public Result<ReportStatisticsDTO> getHandleStatistics(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "学校ID") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "分组ID") @RequestParam(required = false) Long groupId) {
        ReportStatisticsDTO dto = reportService.getHandleStatistics(startDate, endDate, schoolId, groupId);
        return Result.success(dto);
    }

    @Operation(summary = "趋势分析报表")
    @GetMapping("/statistics/trend")
    public Result<ReportStatisticsDTO> getTrendStatistics(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "学校ID") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "分组ID") @RequestParam(required = false) Long groupId) {
        ReportStatisticsDTO dto = reportService.getTrendStatistics(startDate, endDate, schoolId, groupId);
        return Result.success(dto);
    }

    @Operation(summary = "导出报表(Excel)")
    @GetMapping("/statistics/export")
    public ResponseEntity<byte[]> exportReport(
            @Parameter(description = "报表类型:alarm-警情统计,handle-处置统计,trend-趋势分析,compare-对比报表") @RequestParam String reportType,
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

    @Operation(summary = "学校对比")
    @GetMapping("/compare/school")
    public Result<CompareResultVO> getSchoolCompare(
            @Parameter(description = "对比类型: month-月度, quarter-季度") @RequestParam String compareType,
            @Parameter(description = "当前期: YYYY-MM 或 YYYY-QN") @RequestParam String currentPeriod,
            @Parameter(description = "对比期: YYYY-MM 或 YYYY-QN") @RequestParam String comparePeriod,
            @Parameter(description = "学校ID") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "分组ID") @RequestParam(required = false) Long groupId,
            @Parameter(description = "街镇ID") @RequestParam(required = false) Long townId) {
        CompareQueryDTO queryDTO = new CompareQueryDTO();
        queryDTO.setCompareType(compareType);
        queryDTO.setCurrentPeriod(currentPeriod);
        queryDTO.setComparePeriod(comparePeriod);
        queryDTO.setSchoolId(schoolId);
        queryDTO.setGroupId(groupId);
        queryDTO.setTownId(townId);
        CompareResultVO result = reportService.getSchoolCompare(queryDTO);
        return Result.success(result);
    }

    @Operation(summary = "月度对比")
    @GetMapping("/compare/monthly")
    public Result<CompareResultVO> getMonthlyCompare(
            @Parameter(description = "当前月: YYYY-MM") @RequestParam String month1,
            @Parameter(description = "对比月: YYYY-MM") @RequestParam String month2,
            @Parameter(description = "学校ID") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "分组ID") @RequestParam(required = false) Long groupId) {
        CompareResultVO result = reportService.getMonthlyCompare(month1, month2, schoolId, groupId);
        return Result.success(result);
    }

    @Operation(summary = "季度对比")
    @GetMapping("/compare/quarterly")
    public Result<CompareResultVO> getQuarterlyCompare(
            @Parameter(description = "当前季度: YYYY-QN") @RequestParam String quarter1,
            @Parameter(description = "对比季度: YYYY-QN") @RequestParam String quarter2,
            @Parameter(description = "学校ID") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "分组ID") @RequestParam(required = false) Long groupId) {
        CompareResultVO result = reportService.getQuarterlyCompare(quarter1, quarter2, schoolId, groupId);
        return Result.success(result);
    }

    @Operation(summary = "导出对比报表(Excel)")
    @GetMapping("/compare/export")
    public ResponseEntity<byte[]> exportCompareReport(
            @Parameter(description = "对比类型: month-月度, quarter-季度") @RequestParam String compareType,
            @Parameter(description = "当前期: YYYY-MM 或 YYYY-QN") @RequestParam String currentPeriod,
            @Parameter(description = "对比期: YYYY-MM 或 YYYY-QN") @RequestParam String comparePeriod,
            @Parameter(description = "学校ID") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "分组ID") @RequestParam(required = false) Long groupId,
            @Parameter(description = "街镇ID") @RequestParam(required = false) Long townId) {
        CompareQueryDTO queryDTO = new CompareQueryDTO();
        queryDTO.setCompareType(compareType);
        queryDTO.setCurrentPeriod(currentPeriod);
        queryDTO.setComparePeriod(comparePeriod);
        queryDTO.setSchoolId(schoolId);
        queryDTO.setGroupId(groupId);
        queryDTO.setTownId(townId);
        byte[] data = reportService.exportCompareReport(queryDTO);

        String fileName = "对比报表_" + currentPeriod + "_vs_" + comparePeriod + ".xlsx";
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
            case "compare" -> "对比报表";
            default -> "统计报表";
        };
        return typeName + "_" + startDate + "_" + endDate + ".xlsx";
    }

    @Operation(summary = "值守复盘汇总(班次+学校)")
    @GetMapping("/duty-review/summary")
    public Result<DutyReviewSummaryVO> getDutyReviewSummary(
            @Parameter(description = "开始日期", required = true) @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期", required = true) @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "值班班次:1-白班 2-夜班") @RequestParam(required = false) Integer dutyShift,
            @Parameter(description = "值班人ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "街镇ID") @RequestParam(required = false) Long townId,
            @Parameter(description = "学校分组ID") @RequestParam(required = false) Long groupId,
            @Parameter(description = "学校类型") @RequestParam(required = false) Integer schoolType) {
        DutyReviewQueryDTO queryDTO = buildDutyReviewQuery(startDate, endDate, dutyShift, userId, townId, groupId, schoolType);
        DutyReviewSummaryVO result = reportService.getDutyReviewSummary(queryDTO);
        return Result.success(result);
    }

    @Operation(summary = "值守复盘-按班次维度")
    @GetMapping("/duty-review/by-shift")
    public Result<List<DutyReviewShiftVO>> getDutyReviewByShift(
            @Parameter(description = "开始日期", required = true) @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期", required = true) @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "值班班次:1-白班 2-夜班") @RequestParam(required = false) Integer dutyShift,
            @Parameter(description = "值班人ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "街镇ID") @RequestParam(required = false) Long townId,
            @Parameter(description = "学校分组ID") @RequestParam(required = false) Long groupId,
            @Parameter(description = "学校类型") @RequestParam(required = false) Integer schoolType) {
        DutyReviewQueryDTO queryDTO = buildDutyReviewQuery(startDate, endDate, dutyShift, userId, townId, groupId, schoolType);
        List<DutyReviewShiftVO> result = reportService.getDutyReviewByShift(queryDTO);
        return Result.success(result);
    }

    @Operation(summary = "值守复盘-按学校维度")
    @GetMapping("/duty-review/by-school")
    public Result<List<DutyReviewSchoolVO>> getDutyReviewBySchool(
            @Parameter(description = "开始日期", required = true) @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期", required = true) @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "值班班次:1-白班 2-夜班") @RequestParam(required = false) Integer dutyShift,
            @Parameter(description = "值班人ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "街镇ID") @RequestParam(required = false) Long townId,
            @Parameter(description = "学校分组ID") @RequestParam(required = false) Long groupId,
            @Parameter(description = "学校类型") @RequestParam(required = false) Integer schoolType) {
        DutyReviewQueryDTO queryDTO = buildDutyReviewQuery(startDate, endDate, dutyShift, userId, townId, groupId, schoolType);
        List<DutyReviewSchoolVO> result = reportService.getDutyReviewBySchool(queryDTO);
        return Result.success(result);
    }

    @Operation(summary = "导出值守复盘报表(Excel)")
    @GetMapping("/duty-review/export")
    public ResponseEntity<byte[]> exportDutyReview(
            @Parameter(description = "开始日期", required = true) @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期", required = true) @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "值班班次:1-白班 2-夜班") @RequestParam(required = false) Integer dutyShift,
            @Parameter(description = "值班人ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "街镇ID") @RequestParam(required = false) Long townId,
            @Parameter(description = "学校分组ID") @RequestParam(required = false) Long groupId,
            @Parameter(description = "学校类型") @RequestParam(required = false) Integer schoolType) {
        DutyReviewQueryDTO queryDTO = buildDutyReviewQuery(startDate, endDate, dutyShift, userId, townId, groupId, schoolType);
        byte[] data = reportService.exportDutyReview(queryDTO);

        String fileName = "值守复盘报表_" + startDate + "_" + endDate + ".xlsx";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", encodedFileName);
        headers.setContentLength(data.length);

        return ResponseEntity.ok().headers(headers).body(data);
    }

    private DutyReviewQueryDTO buildDutyReviewQuery(LocalDate startDate, LocalDate endDate,
                                                     Integer dutyShift, Long userId,
                                                     Long townId, Long groupId, Integer schoolType) {
        DutyReviewQueryDTO queryDTO = new DutyReviewQueryDTO();
        queryDTO.setStartDate(startDate);
        queryDTO.setEndDate(endDate);
        queryDTO.setDutyShift(dutyShift);
        queryDTO.setUserId(userId);
        queryDTO.setTownId(townId);
        queryDTO.setGroupId(groupId);
        queryDTO.setSchoolType(schoolType);
        return queryDTO;
    }
}

package com.safetycampus.overview.controller;

import com.safetycampus.common.result.Result;
import com.safetycampus.overview.dto.OverviewQueryDTO;
import com.safetycampus.overview.service.OverviewService;
import com.safetycampus.overview.vo.OverviewDashboardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "态势总览接口", description = "县区态势总览大屏、统计分布、警情列表等功能")
@RestController
@RequestMapping("/api/overview")
public class OverviewController {

    @Resource
    private OverviewService overviewService;

    @Operation(summary = "大屏总览数据")
    @GetMapping("/dashboard")
    public Result<OverviewDashboardVO> getDashboard(
            @Parameter(description = "学校ID") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "街镇ID") @RequestParam(required = false) Long townId,
            @Parameter(description = "学校类型:1-幼儿园,2-小学,3-初中,4-高中,5-中职,6-高校") @RequestParam(required = false) Integer schoolType,
            @Parameter(description = "警情类型:1-紧急求助,2-火灾,3-治安,4-校园欺凌,5-食物中毒,6-自然灾害,7-其他") @RequestParam(required = false) Integer alarmType,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        OverviewQueryDTO queryDTO = buildQueryDTO(schoolId, townId, schoolType, alarmType, startTime, endTime);
        OverviewDashboardVO vo = overviewService.getDashboard(queryDTO);
        return Result.success(vo);
    }

    @Operation(summary = "学校类型分布")
    @GetMapping("/school-type-distribution")
    public Result<List<OverviewDashboardVO.SchoolTypeDistribution>> getSchoolTypeDistribution(
            @Parameter(description = "学校ID") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "街镇ID") @RequestParam(required = false) Long townId,
            @Parameter(description = "学校类型:1-幼儿园,2-小学,3-初中,4-高中,5-中职,6-高校") @RequestParam(required = false) Integer schoolType,
            @Parameter(description = "警情类型:1-紧急求助,2-火灾,3-治安,4-校园欺凌,5-食物中毒,6-自然灾害,7-其他") @RequestParam(required = false) Integer alarmType,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        OverviewQueryDTO queryDTO = buildQueryDTO(schoolId, townId, schoolType, alarmType, startTime, endTime);
        List<OverviewDashboardVO.SchoolTypeDistribution> list = overviewService.getSchoolTypeDistribution(queryDTO);
        return Result.success(list);
    }

    @Operation(summary = "街镇分布")
    @GetMapping("/town-distribution")
    public Result<List<OverviewDashboardVO.TownDistribution>> getTownDistribution(
            @Parameter(description = "学校ID") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "街镇ID") @RequestParam(required = false) Long townId,
            @Parameter(description = "学校类型:1-幼儿园,2-小学,3-初中,4-高中,5-中职,6-高校") @RequestParam(required = false) Integer schoolType,
            @Parameter(description = "警情类型:1-紧急求助,2-火灾,3-治安,4-校园欺凌,5-食物中毒,6-自然灾害,7-其他") @RequestParam(required = false) Integer alarmType,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        OverviewQueryDTO queryDTO = buildQueryDTO(schoolId, townId, schoolType, alarmType, startTime, endTime);
        List<OverviewDashboardVO.TownDistribution> list = overviewService.getTownDistribution(queryDTO);
        return Result.success(list);
    }

    @Operation(summary = "警情类型分布")
    @GetMapping("/alarm-type-distribution")
    public Result<List<OverviewDashboardVO.AlarmTypeDistribution>> getAlarmTypeDistribution(
            @Parameter(description = "学校ID") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "街镇ID") @RequestParam(required = false) Long townId,
            @Parameter(description = "学校类型:1-幼儿园,2-小学,3-初中,4-高中,5-中职,6-高校") @RequestParam(required = false) Integer schoolType,
            @Parameter(description = "警情类型:1-紧急求助,2-火灾,3-治安,4-校园欺凌,5-食物中毒,6-自然灾害,7-其他") @RequestParam(required = false) Integer alarmType,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        OverviewQueryDTO queryDTO = buildQueryDTO(schoolId, townId, schoolType, alarmType, startTime, endTime);
        List<OverviewDashboardVO.AlarmTypeDistribution> list = overviewService.getAlarmTypeDistribution(queryDTO);
        return Result.success(list);
    }

    @Operation(summary = "派出所联动进展")
    @GetMapping("/police-progress")
    public Result<List<OverviewDashboardVO.PoliceStationProgress>> getPoliceStationProgress(
            @Parameter(description = "街镇ID") @RequestParam(required = false) Long townId,
            @Parameter(description = "学校类型:1-幼儿园,2-小学,3-初中,4-高中,5-中职,6-高校") @RequestParam(required = false) Integer schoolType,
            @Parameter(description = "警情类型:1-紧急求助,2-火灾,3-治安,4-校园欺凌,5-食物中毒,6-自然灾害,7-其他") @RequestParam(required = false) Integer alarmType,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        OverviewQueryDTO queryDTO = buildQueryDTO(null, townId, schoolType, alarmType, startTime, endTime);
        List<OverviewDashboardVO.PoliceStationProgress> list = overviewService.getPoliceStationProgress(queryDTO);
        return Result.success(list);
    }

    @Operation(summary = "近期警情列表")
    @GetMapping("/recent-alarms")
    public Result<List<OverviewDashboardVO.RecentAlarm>> getRecentAlarms(
            @Parameter(description = "学校ID") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "街镇ID") @RequestParam(required = false) Long townId,
            @Parameter(description = "学校类型:1-幼儿园,2-小学,3-初中,4-高中,5-中职,6-高校") @RequestParam(required = false) Integer schoolType,
            @Parameter(description = "警情类型:1-紧急求助,2-火灾,3-治安,4-校园欺凌,5-食物中毒,6-自然灾害,7-其他") @RequestParam(required = false) Integer alarmType,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        OverviewQueryDTO queryDTO = buildQueryDTO(schoolId, townId, schoolType, alarmType, startTime, endTime);
        List<OverviewDashboardVO.RecentAlarm> list = overviewService.getRecentAlarms(queryDTO);
        return Result.success(list);
    }

    @Operation(summary = "超时警情列表")
    @GetMapping("/timeout-alarms")
    public Result<List<OverviewDashboardVO.TimeoutAlarm>> getTimeoutAlarms(
            @Parameter(description = "学校ID") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "街镇ID") @RequestParam(required = false) Long townId,
            @Parameter(description = "学校类型:1-幼儿园,2-小学,3-初中,4-高中,5-中职,6-高校") @RequestParam(required = false) Integer schoolType,
            @Parameter(description = "警情类型:1-紧急求助,2-火灾,3-治安,4-校园欺凌,5-食物中毒,6-自然灾害,7-其他") @RequestParam(required = false) Integer alarmType,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        OverviewQueryDTO queryDTO = buildQueryDTO(schoolId, townId, schoolType, alarmType, startTime, endTime);
        List<OverviewDashboardVO.TimeoutAlarm> list = overviewService.getTimeoutAlarms(queryDTO);
        return Result.success(list);
    }

    private OverviewQueryDTO buildQueryDTO(Long schoolId, Long townId, Integer schoolType, Integer alarmType,
                                           LocalDateTime startTime, LocalDateTime endTime) {
        OverviewQueryDTO queryDTO = new OverviewQueryDTO();
        queryDTO.setSchoolId(schoolId);
        queryDTO.setTownId(townId);
        queryDTO.setSchoolType(schoolType);
        queryDTO.setAlarmType(alarmType);
        queryDTO.setStartTime(startTime);
        queryDTO.setEndTime(endTime);
        return queryDTO;
    }
}

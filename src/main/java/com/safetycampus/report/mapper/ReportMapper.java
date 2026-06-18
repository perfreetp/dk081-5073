package com.safetycampus.report.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {

    List<Map<String, Object>> selectAlarmStatsByPeriod(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("schoolId") Long schoolId,
            @Param("groupId") Long groupId,
            @Param("townId") Long townId);

    List<Map<String, Object>> selectRankByPeriod(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("schoolId") Long schoolId,
            @Param("groupId") Long groupId,
            @Param("townId") Long townId);

    List<Map<String, Object>> selectTimeoutStatsByPeriod(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("schoolId") Long schoolId,
            @Param("groupId") Long groupId,
            @Param("townId") Long townId);

    List<Map<String, Object>> selectShiftReview(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("dutyShift") Integer dutyShift,
            @Param("userId") Long userId,
            @Param("townId") Long townId,
            @Param("groupId") Long groupId,
            @Param("schoolType") Integer schoolType);

    List<Map<String, Object>> selectSchoolReview(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("dutyShift") Integer dutyShift,
            @Param("userId") Long userId,
            @Param("townId") Long townId,
            @Param("groupId") Long groupId,
            @Param("schoolType") Integer schoolType);

    List<Map<String, Object>> selectRiskEventList(
            @Param("dutyDate") LocalDate dutyDate,
            @Param("shiftType") Integer shiftType,
            @Param("townId") Long townId,
            @Param("groupId") Long groupId,
            @Param("schoolType") Integer schoolType);

    Long selectRemindCountByRange(
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("townId") Long townId,
            @Param("groupId") Long groupId,
            @Param("schoolType") Integer schoolType);

    List<Map<String, Object>> selectPoliceFeedbackStats(
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("townId") Long townId,
            @Param("groupId") Long groupId,
            @Param("schoolType") Integer schoolType);
}

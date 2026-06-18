package com.safetycampus.report.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
}

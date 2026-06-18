package com.safetycampus.overview.mapper;

import com.safetycampus.overview.dto.OverviewQueryDTO;
import com.safetycampus.overview.vo.OverviewDashboardVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OverviewMapper {

    Long selectTodayAlarmCount(@Param("query") OverviewQueryDTO queryDTO);

    Long selectTodayCriticalCount(@Param("query") OverviewQueryDTO queryDTO);

    Long selectTodayTimeoutCount(@Param("query") OverviewQueryDTO queryDTO);

    Long selectProcessingCount(@Param("query") OverviewQueryDTO queryDTO);

    Long selectHandledCount(@Param("query") OverviewQueryDTO queryDTO);

    Long selectClosedCount(@Param("query") OverviewQueryDTO queryDTO);

    Map<String, Object> selectAvgTime(@Param("query") OverviewQueryDTO queryDTO);

    List<OverviewDashboardVO.SchoolTypeDistribution> selectSchoolTypeDistribution(@Param("query") OverviewQueryDTO queryDTO);

    List<OverviewDashboardVO.TownDistribution> selectTownDistribution(@Param("query") OverviewQueryDTO queryDTO);

    List<OverviewDashboardVO.AlarmTypeDistribution> selectAlarmTypeDistribution(@Param("query") OverviewQueryDTO queryDTO);

    List<OverviewDashboardVO.PoliceStationProgress> selectPoliceStationProgress(@Param("query") OverviewQueryDTO queryDTO);

    List<OverviewDashboardVO.RecentAlarm> selectRecentAlarms(@Param("query") OverviewQueryDTO queryDTO);

    List<OverviewDashboardVO.TimeoutAlarm> selectTimeoutAlarms(@Param("query") OverviewQueryDTO queryDTO);
}

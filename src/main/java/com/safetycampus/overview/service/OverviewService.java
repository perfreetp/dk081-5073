package com.safetycampus.overview.service;

import com.safetycampus.overview.dto.OverviewQueryDTO;
import com.safetycampus.overview.vo.OverviewDashboardVO;

import java.util.List;

public interface OverviewService {

    OverviewDashboardVO getDashboard(OverviewQueryDTO queryDTO);

    List<OverviewDashboardVO.SchoolTypeDistribution> getSchoolTypeDistribution(OverviewQueryDTO queryDTO);

    List<OverviewDashboardVO.TownDistribution> getTownDistribution(OverviewQueryDTO queryDTO);

    List<OverviewDashboardVO.AlarmTypeDistribution> getAlarmTypeDistribution(OverviewQueryDTO queryDTO);

    List<OverviewDashboardVO.PoliceStationProgress> getPoliceStationProgress(OverviewQueryDTO queryDTO);

    List<OverviewDashboardVO.RecentAlarm> getRecentAlarms(OverviewQueryDTO queryDTO);

    List<OverviewDashboardVO.TimeoutAlarm> getTimeoutAlarms(OverviewQueryDTO queryDTO);
}

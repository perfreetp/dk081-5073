package com.safetycampus.overview.service.impl;

import com.safetycampus.overview.dto.OverviewQueryDTO;
import com.safetycampus.overview.mapper.OverviewMapper;
import com.safetycampus.overview.service.OverviewService;
import com.safetycampus.overview.vo.OverviewDashboardVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OverviewServiceImpl implements OverviewService {

    @Resource
    private OverviewMapper overviewMapper;

    @Override
    public OverviewDashboardVO getDashboard(OverviewQueryDTO queryDTO) {
        OverviewDashboardVO vo = new OverviewDashboardVO();

        vo.setTodayAlarmCount(overviewMapper.selectTodayAlarmCount(queryDTO));
        vo.setTodayCriticalCount(overviewMapper.selectTodayCriticalCount(queryDTO));
        vo.setTodayTimeoutCount(overviewMapper.selectTodayTimeoutCount(queryDTO));
        vo.setProcessingCount(overviewMapper.selectProcessingCount(queryDTO));
        vo.setHandledCount(overviewMapper.selectHandledCount(queryDTO));
        vo.setClosedCount(overviewMapper.selectClosedCount(queryDTO));

        Map<String, Object> avgTime = overviewMapper.selectAvgTime(queryDTO);
        if (avgTime != null) {
            Object avgResp = avgTime.get("avgResponseTime");
            Object avgHandle = avgTime.get("avgHandleTime");
            vo.setAvgResponseTime(avgResp != null ?
                    new BigDecimal(avgResp.toString()).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            vo.setAvgHandleTime(avgHandle != null ?
                    new BigDecimal(avgHandle.toString()).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        } else {
            vo.setAvgResponseTime(BigDecimal.ZERO);
            vo.setAvgHandleTime(BigDecimal.ZERO);
        }

        vo.setSchoolTypeDistribution(getSchoolTypeDistribution(queryDTO));
        vo.setTownDistribution(getTownDistribution(queryDTO));
        vo.setAlarmTypeDistribution(getAlarmTypeDistribution(queryDTO));
        vo.setPoliceStationProgress(getPoliceStationProgress(queryDTO));
        vo.setRecentAlarms(getRecentAlarms(queryDTO));
        vo.setTimeoutAlarmList(getTimeoutAlarms(queryDTO));

        return vo;
    }

    @Override
    public List<OverviewDashboardVO.SchoolTypeDistribution> getSchoolTypeDistribution(OverviewQueryDTO queryDTO) {
        return overviewMapper.selectSchoolTypeDistribution(queryDTO);
    }

    @Override
    public List<OverviewDashboardVO.TownDistribution> getTownDistribution(OverviewQueryDTO queryDTO) {
        return overviewMapper.selectTownDistribution(queryDTO);
    }

    @Override
    public List<OverviewDashboardVO.AlarmTypeDistribution> getAlarmTypeDistribution(OverviewQueryDTO queryDTO) {
        return overviewMapper.selectAlarmTypeDistribution(queryDTO);
    }

    @Override
    public List<OverviewDashboardVO.PoliceStationProgress> getPoliceStationProgress(OverviewQueryDTO queryDTO) {
        return overviewMapper.selectPoliceStationProgress(queryDTO);
    }

    @Override
    public List<OverviewDashboardVO.RecentAlarm> getRecentAlarms(OverviewQueryDTO queryDTO) {
        return overviewMapper.selectRecentAlarms(queryDTO);
    }

    @Override
    public List<OverviewDashboardVO.TimeoutAlarm> getTimeoutAlarms(OverviewQueryDTO queryDTO) {
        return overviewMapper.selectTimeoutAlarms(queryDTO);
    }
}

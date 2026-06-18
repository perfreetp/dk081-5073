package com.safetycampus.report.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class ReportStatisticsDTO implements Serializable {

    private Long totalAlarms;

    private Long pendingAlarms;

    private Long handledAlarms;

    private Long criticalAlarms;

    private Long closedAlarms;

    private BigDecimal avgResponseTime;

    private BigDecimal avgHandleTime;

    private Long timeoutCount;

    private List<Map<String, Object>> alarmTypeStats;

    private List<Map<String, Object>> alarmLevelStats;

    private List<Map<String, Object>> schoolStats;

    private List<Map<String, Object>> trendStats;

    private List<Map<String, Object>> handleStats;
}

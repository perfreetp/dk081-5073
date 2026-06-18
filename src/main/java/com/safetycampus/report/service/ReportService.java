package com.safetycampus.report.service;

import com.safetycampus.report.dto.ReportStatisticsDTO;

import java.time.LocalDate;

public interface ReportService {

    ReportStatisticsDTO getAlarmStatistics(LocalDate startDate, LocalDate endDate, Long schoolId, Long groupId);

    ReportStatisticsDTO getHandleStatistics(LocalDate startDate, LocalDate endDate, Long schoolId, Long groupId);

    ReportStatisticsDTO getTrendStatistics(LocalDate startDate, LocalDate endDate, Long schoolId, Long groupId);

    byte[] exportReport(String reportType, LocalDate startDate, LocalDate endDate, Long schoolId, Long groupId);
}

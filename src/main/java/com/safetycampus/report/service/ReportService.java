package com.safetycampus.report.service;

import com.safetycampus.report.dto.CompareQueryDTO;
import com.safetycampus.report.dto.ReportStatisticsDTO;
import com.safetycampus.report.vo.CompareResultVO;

import java.time.LocalDate;

public interface ReportService {

    ReportStatisticsDTO getAlarmStatistics(LocalDate startDate, LocalDate endDate, Long schoolId, Long groupId);

    ReportStatisticsDTO getHandleStatistics(LocalDate startDate, LocalDate endDate, Long schoolId, Long groupId);

    ReportStatisticsDTO getTrendStatistics(LocalDate startDate, LocalDate endDate, Long schoolId, Long groupId);

    byte[] exportReport(String reportType, LocalDate startDate, LocalDate endDate, Long schoolId, Long groupId);

    CompareResultVO getSchoolCompare(CompareQueryDTO queryDTO);

    CompareResultVO getMonthlyCompare(String month1, String month2, Long schoolId, Long groupId);

    CompareResultVO getQuarterlyCompare(String quarter1, String quarter2, Long schoolId, Long groupId);

    byte[] exportCompareReport(CompareQueryDTO queryDTO);
}

package com.safetycampus.report.service;

import com.safetycampus.report.dto.CompareQueryDTO;
import com.safetycampus.report.dto.DutyReviewQueryDTO;
import com.safetycampus.report.dto.ReportStatisticsDTO;
import com.safetycampus.report.vo.CompareResultVO;
import com.safetycampus.report.vo.DutyReviewSchoolVO;
import com.safetycampus.report.vo.DutyReviewShiftVO;
import com.safetycampus.report.vo.DutyReviewSummaryVO;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    ReportStatisticsDTO getAlarmStatistics(LocalDate startDate, LocalDate endDate, Long schoolId, Long groupId);

    ReportStatisticsDTO getHandleStatistics(LocalDate startDate, LocalDate endDate, Long schoolId, Long groupId);

    ReportStatisticsDTO getTrendStatistics(LocalDate startDate, LocalDate endDate, Long schoolId, Long groupId);

    byte[] exportReport(String reportType, LocalDate startDate, LocalDate endDate, Long schoolId, Long groupId);

    CompareResultVO getSchoolCompare(CompareQueryDTO queryDTO);

    CompareResultVO getMonthlyCompare(String month1, String month2, Long schoolId, Long groupId);

    CompareResultVO getQuarterlyCompare(String quarter1, String quarter2, Long schoolId, Long groupId);

    byte[] exportCompareReport(CompareQueryDTO queryDTO);

    DutyReviewSummaryVO getDutyReviewSummary(DutyReviewQueryDTO queryDTO);

    List<DutyReviewShiftVO> getDutyReviewByShift(DutyReviewQueryDTO queryDTO);

    List<DutyReviewSchoolVO> getDutyReviewBySchool(DutyReviewQueryDTO queryDTO);

    byte[] exportDutyReview(DutyReviewQueryDTO queryDTO);
}

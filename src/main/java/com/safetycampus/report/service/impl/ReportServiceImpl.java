package com.safetycampus.report.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.safetycampus.alarm.entity.AlarmRecord;
import com.safetycampus.alarm.enums.AlarmLevelEnum;
import com.safetycampus.alarm.enums.AlarmStatusEnum;
import com.safetycampus.alarm.enums.AlarmTypeEnum;
import com.safetycampus.alarm.mapper.AlarmRecordMapper;
import com.safetycampus.report.dto.CompareQueryDTO;
import com.safetycampus.report.dto.DutyReviewQueryDTO;
import com.safetycampus.report.dto.ReportStatisticsDTO;
import com.safetycampus.report.mapper.ReportMapper;
import com.safetycampus.common.exception.BusinessException;
import com.safetycampus.common.result.ResultCode;
import com.safetycampus.report.service.ReportService;
import com.safetycampus.report.vo.CompareResultVO;
import com.safetycampus.report.vo.DutyReviewSchoolVO;
import com.safetycampus.report.vo.DutyReviewShiftVO;
import com.safetycampus.report.vo.DutyReviewSummaryVO;
import com.safetycampus.report.vo.SchoolCompareItemVO;
import com.safetycampus.school.entity.SchoolInfo;
import com.safetycampus.school.mapper.SchoolInfoMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    @Resource
    private AlarmRecordMapper alarmRecordMapper;

    @Resource
    private SchoolInfoMapper schoolInfoMapper;

    @Resource
    private ReportMapper reportMapper;

    @Override
    public ReportStatisticsDTO getAlarmStatistics(LocalDate startDate, LocalDate endDate, Long schoolId, Long groupId) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(23, 59, 59);

        List<Long> schoolIds = getSchoolIds(schoolId, groupId);

        LambdaQueryWrapper<AlarmRecord> wrapper = buildQueryWrapper(startTime, endTime, schoolIds);
        List<AlarmRecord> records = alarmRecordMapper.selectList(wrapper);

        ReportStatisticsDTO dto = new ReportStatisticsDTO();
        dto.setTotalAlarms((long) records.size());
        dto.setPendingAlarms(countByStatus(records, AlarmStatusEnum.PENDING.getCode()));
        dto.setHandledAlarms(countByStatus(records, AlarmStatusEnum.HANDLED.getCode()));
        dto.setClosedAlarms(countByStatus(records, AlarmStatusEnum.CLOSED.getCode()));
        dto.setCriticalAlarms(countByLevel(records, AlarmLevelEnum.CRITICAL.getCode()));
        dto.setTimeoutCount((long) countTimeoutRecords(records));

        dto.setAlarmTypeStats(getAlarmTypeStats(records));
        dto.setAlarmLevelStats(getAlarmLevelStats(records));
        dto.setSchoolStats(getSchoolStats(records));

        return dto;
    }

    @Override
    public ReportStatisticsDTO getHandleStatistics(LocalDate startDate, LocalDate endDate, Long schoolId, Long groupId) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(23, 59, 59);

        List<Long> schoolIds = getSchoolIds(schoolId, groupId);

        LambdaQueryWrapper<AlarmRecord> wrapper = buildQueryWrapper(startTime, endTime, schoolIds);
        List<AlarmRecord> records = alarmRecordMapper.selectList(wrapper);

        ReportStatisticsDTO dto = new ReportStatisticsDTO();
        dto.setTotalAlarms((long) records.size());
        dto.setHandledAlarms(countByStatus(records, AlarmStatusEnum.HANDLED.getCode()));
        dto.setClosedAlarms(countByStatus(records, AlarmStatusEnum.CLOSED.getCode()));
        dto.setTimeoutCount((long) countTimeoutRecords(records));

        int[] responseTimes = calculateResponseTimes(records);
        int[] handleTimes = calculateHandleTimes(records);

        dto.setAvgResponseTime(responseTimes[1] > 0 ?
                BigDecimal.valueOf(responseTimes[0]).divide(BigDecimal.valueOf(responseTimes[1]), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        dto.setAvgHandleTime(handleTimes[1] > 0 ?
                BigDecimal.valueOf(handleTimes[0]).divide(BigDecimal.valueOf(handleTimes[1]), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        dto.setHandleStats(getHandleStats(records));

        return dto;
    }

    @Override
    public ReportStatisticsDTO getTrendStatistics(LocalDate startDate, LocalDate endDate, Long schoolId, Long groupId) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(23, 59, 59);

        List<Long> schoolIds = getSchoolIds(schoolId, groupId);

        LambdaQueryWrapper<AlarmRecord> wrapper = buildQueryWrapper(startTime, endTime, schoolIds);
        List<AlarmRecord> records = alarmRecordMapper.selectList(wrapper);

        ReportStatisticsDTO dto = new ReportStatisticsDTO();
        dto.setTotalAlarms((long) records.size());
        dto.setTrendStats(getTrendStats(records, startDate, endDate));

        return dto;
    }

    @Override
    public byte[] exportReport(String reportType, LocalDate startDate, LocalDate endDate, Long schoolId, Long groupId) {
        ReportStatisticsDTO data;
        String fileName;

        switch (reportType) {
            case "alarm":
                data = getAlarmStatistics(startDate, endDate, schoolId, groupId);
                fileName = "警情统计报表_" + startDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + endDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
                return exportAlarmReport(data, fileName, startDate, endDate, schoolId, groupId);
            case "handle":
                data = getHandleStatistics(startDate, endDate, schoolId, groupId);
                fileName = "处置统计报表_" + startDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + endDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
                return exportHandleReport(data, fileName, startDate, endDate, schoolId, groupId);
            case "trend":
                data = getTrendStatistics(startDate, endDate, schoolId, groupId);
                fileName = "趋势分析报表_" + startDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + endDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
                return exportTrendReport(data, fileName, startDate, endDate, schoolId, groupId);
            case "compare":
                CompareQueryDTO queryDTO = new CompareQueryDTO();
                queryDTO.setSchoolId(schoolId);
                queryDTO.setGroupId(groupId);
                queryDTO.setCurrentPeriod(startDate.format(DateTimeFormatter.ofPattern("yyyy-MM")));
                queryDTO.setComparePeriod(startDate.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM")));
                queryDTO.setCompareType("month");
                return exportCompareReport(queryDTO);
            default:
                throw new IllegalArgumentException("不支持的报表类型");
        }
    }

    private List<Long> getSchoolIds(Long schoolId, Long groupId) {
        if (schoolId != null) {
            return Collections.singletonList(schoolId);
        }
        if (groupId != null) {
            LambdaQueryWrapper<SchoolInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SchoolInfo::getGroupId, groupId);
            wrapper.eq(SchoolInfo::getStatus, 1);
            return schoolInfoMapper.selectList(wrapper).stream()
                    .map(SchoolInfo::getId)
                    .collect(Collectors.toList());
        }
        return null;
    }

    private LambdaQueryWrapper<AlarmRecord> buildQueryWrapper(LocalDateTime startTime, LocalDateTime endTime, List<Long> schoolIds) {
        LambdaQueryWrapper<AlarmRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(AlarmRecord::getCreatedAt, startTime);
        wrapper.le(AlarmRecord::getCreatedAt, endTime);
        wrapper.eq(AlarmRecord::getDeleted, 0);
        if (schoolIds != null && !schoolIds.isEmpty()) {
            wrapper.in(AlarmRecord::getSchoolId, schoolIds);
        }
        return wrapper;
    }

    private long countByStatus(List<AlarmRecord> records, Integer status) {
        return records.stream()
                .filter(r -> status.equals(r.getStatus()))
                .count();
    }

    private long countByLevel(List<AlarmRecord> records, Integer level) {
        return records.stream()
                .filter(r -> level.equals(r.getAlarmLevel()))
                .count();
    }

    private int countTimeoutRecords(List<AlarmRecord> records) {
        int count = 0;
        for (AlarmRecord record : records) {
            if (record.getFirstResponseAt() != null) {
                long seconds = java.time.Duration.between(record.getCreatedAt(), record.getFirstResponseAt()).getSeconds();
                int timeoutSeconds = AlarmLevelEnum.CRITICAL.getCode().equals(record.getAlarmLevel()) ? 300 :
                                    AlarmLevelEnum.MAJOR.getCode().equals(record.getAlarmLevel()) ? 600 : 1800;
                if (seconds > timeoutSeconds) {
                    count++;
                }
            }
        }
        return count;
    }

    private int[] calculateResponseTimes(List<AlarmRecord> records) {
        int totalTime = 0;
        int count = 0;
        for (AlarmRecord record : records) {
            if (record.getFirstResponseAt() != null) {
                long seconds = java.time.Duration.between(record.getCreatedAt(), record.getFirstResponseAt()).getSeconds();
                totalTime += seconds;
                count++;
            }
        }
        return new int[]{totalTime, count};
    }

    private int[] calculateHandleTimes(List<AlarmRecord> records) {
        int totalTime = 0;
        int count = 0;
        for (AlarmRecord record : records) {
            if (record.getHandledAt() != null && record.getFirstResponseAt() != null) {
                long seconds = java.time.Duration.between(record.getFirstResponseAt(), record.getHandledAt()).getSeconds();
                totalTime += seconds;
                count++;
            }
        }
        return new int[]{totalTime, count};
    }

    private List<Map<String, Object>> getAlarmTypeStats(List<AlarmRecord> records) {
        Map<Integer, Long> typeCount = records.stream()
                .collect(Collectors.groupingBy(AlarmRecord::getAlarmType, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (AlarmTypeEnum typeEnum : AlarmTypeEnum.values()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", typeEnum.getCode());
            map.put("typeName", typeEnum.getDesc());
            map.put("count", typeCount.getOrDefault(typeEnum.getCode(), 0L));
            result.add(map);
        }
        return result;
    }

    private List<Map<String, Object>> getAlarmLevelStats(List<AlarmRecord> records) {
        Map<Integer, Long> levelCount = records.stream()
                .collect(Collectors.groupingBy(AlarmRecord::getAlarmLevel, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (AlarmLevelEnum levelEnum : AlarmLevelEnum.values()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("level", levelEnum.getCode());
            map.put("levelName", levelEnum.getDesc());
            map.put("count", levelCount.getOrDefault(levelEnum.getCode(), 0L));
            result.add(map);
        }
        return result;
    }

    private List<Map<String, Object>> getSchoolStats(List<AlarmRecord> records) {
        Map<Long, Long> schoolCount = records.stream()
                .collect(Collectors.groupingBy(AlarmRecord::getSchoolId, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        List<Long> topSchoolIds = schoolCount.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        for (Long sid : topSchoolIds) {
            SchoolInfo school = schoolInfoMapper.selectById(sid);
            if (school != null) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("schoolId", sid);
                map.put("schoolName", school.getSchoolName());
                map.put("count", schoolCount.get(sid));
                result.add(map);
            }
        }
        return result;
    }

    private List<Map<String, Object>> getHandleStats(List<AlarmRecord> records) {
        List<Map<String, Object>> result = new ArrayList<>();

        long handled = countByStatus(records, AlarmStatusEnum.HANDLED.getCode());
        long closed = countByStatus(records, AlarmStatusEnum.CLOSED.getCode());
        long total = records.size();

        Map<String, Object> handleRate = new LinkedHashMap<>();
        handleRate.put("item", "处置率");
        handleRate.put("value", total > 0 ? BigDecimal.valueOf(handled + closed)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP) + "%" : "0%");
        result.add(handleRate);

        Map<String, Object> closeRate = new LinkedHashMap<>();
        closeRate.put("item", "结案率");
        closeRate.put("value", total > 0 ? BigDecimal.valueOf(closed)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP) + "%" : "0%");
        result.add(closeRate);

        int timeoutCount = countTimeoutRecords(records);
        Map<String, Object> timeoutRate = new LinkedHashMap<>();
        timeoutRate.put("item", "超时率");
        timeoutRate.put("value", total > 0 ? BigDecimal.valueOf(timeoutCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP) + "%" : "0%");
        result.add(timeoutRate);

        return result;
    }

    private List<Map<String, Object>> getTrendStats(List<AlarmRecord> records, LocalDate startDate, LocalDate endDate) {
        Map<String, Long> dateCount = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        Collectors.counting()
                ));

        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            String dateStr = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("date", dateStr);
            map.put("count", dateCount.getOrDefault(dateStr, 0L));
            result.add(map);
            current = current.plusDays(1);
        }
        return result;
    }

    private byte[] exportAlarmReport(ReportStatisticsDTO data, String fileName, LocalDate startDate, LocalDate endDate, Long schoolId, Long groupId) {
        try (ExcelWriter writer = ExcelUtil.getWriter()) {
            writer.addHeaderAlias("item", "统计项");
            writer.addHeaderAlias("value", "数值");
            writer.addHeaderAlias("compareValue", "上期数值");
            writer.addHeaderAlias("changeRate", "环比变化");

            String currentPeriod = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            String comparePeriod = startDate.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));
            Map<String, Map<String, Object>> compareDataMap = getCompareDataMap(currentPeriod, comparePeriod, schoolId, groupId);
            ReportStatisticsDTO compareData = getAlarmStatistics(startDate.minusMonths(1), endDate.minusMonths(1), schoolId, groupId);

            List<Map<String, Object>> summary = new ArrayList<>();
            addSummaryItem(summary, "统计周期", startDate + " 至 " + endDate);
            addCompareSummaryItem(summary, "总报警数", data.getTotalAlarms(), compareData.getTotalAlarms());
            addCompareSummaryItem(summary, "待处置警情", data.getPendingAlarms(), compareData.getPendingAlarms());
            addCompareSummaryItem(summary, "已处置警情", data.getHandledAlarms(), compareData.getHandledAlarms());
            addCompareSummaryItem(summary, "已关闭警情", data.getClosedAlarms(), compareData.getClosedAlarms());
            addCompareSummaryItem(summary, "重大警情", data.getCriticalAlarms(), compareData.getCriticalAlarms());
            addCompareSummaryItem(summary, "超时警情", data.getTimeoutCount(), compareData.getTimeoutCount());

            writer.write(summary, true);

            writer.setCurrentRow(writer.getCurrentRow() + 2);
            writer.merge(1, "警情类型统计");
            writer.setCurrentRow(writer.getCurrentRow() + 1);
            writer.addHeaderAlias("typeName", "报警类型");
            writer.addHeaderAlias("count", "数量");
            writer.write(data.getAlarmTypeStats(), true);

            writer.setCurrentRow(writer.getCurrentRow() + 2);
            writer.merge(1, "警情级别统计");
            writer.setCurrentRow(writer.getCurrentRow() + 1);
            writer.addHeaderAlias("levelName", "警情级别");
            writer.addHeaderAlias("count", "数量");
            writer.write(data.getAlarmLevelStats(), true);

            writer.setCurrentRow(writer.getCurrentRow() + 2);
            writer.merge(1, "学校报警TOP10");
            writer.setCurrentRow(writer.getCurrentRow() + 1);
            writer.addHeaderAlias("schoolName", "学校名称");
            writer.addHeaderAlias("count", "报警数量");
            writer.addHeaderAlias("rankChange", "排名变化");
            writer.addHeaderAlias("riskSummary", "高风险原因摘要");

            List<Map<String, Object>> enhancedSchoolStats = new ArrayList<>();
            for (Map<String, Object> stat : data.getSchoolStats()) {
                Long sid = (Long) stat.get("schoolId");
                Map<String, Object> enhancedStat = new LinkedHashMap<>(stat);
                if (compareDataMap.containsKey(String.valueOf(sid))) {
                    Map<String, Object> cd = compareDataMap.get(String.valueOf(sid));
                    enhancedStat.put("rankChange", formatRankChange((Integer) cd.get("rankChange")));
                    enhancedStat.put("riskSummary", cd.get("riskSummary"));
                } else {
                    enhancedStat.put("rankChange", "-");
                    enhancedStat.put("riskSummary", "-");
                }
                enhancedSchoolStats.add(enhancedStat);
            }
            writer.write(enhancedSchoolStats, true);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writer.flush(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("导出警情统计报表失败", e);
            throw BusinessException.of(ResultCode.EXPORT_FAILED);
        }
    }

    private byte[] exportHandleReport(ReportStatisticsDTO data, String fileName, LocalDate startDate, LocalDate endDate, Long schoolId, Long groupId) {
        try (ExcelWriter writer = ExcelUtil.getWriter()) {
            writer.addHeaderAlias("item", "统计项");
            writer.addHeaderAlias("value", "数值");
            writer.addHeaderAlias("compareValue", "上期数值");
            writer.addHeaderAlias("changeRate", "环比变化");

            String currentPeriod = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            String comparePeriod = startDate.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));
            Map<String, Map<String, Object>> compareDataMap = getCompareDataMap(currentPeriod, comparePeriod, schoolId, groupId);
            ReportStatisticsDTO compareData = getHandleStatistics(startDate.minusMonths(1), endDate.minusMonths(1), schoolId, groupId);

            List<Map<String, Object>> summary = new ArrayList<>();
            addSummaryItem(summary, "统计周期", startDate + " 至 " + endDate);
            addCompareSummaryItem(summary, "总报警数", data.getTotalAlarms(), compareData.getTotalAlarms());
            addCompareSummaryItem(summary, "已处置警情", data.getHandledAlarms(), compareData.getHandledAlarms());
            addCompareSummaryItem(summary, "已关闭警情", data.getClosedAlarms(), compareData.getClosedAlarms());
            addCompareSummaryItem(summary, "平均响应时间(秒)", data.getAvgResponseTime(), compareData.getAvgResponseTime());
            addCompareSummaryItem(summary, "平均处置时间(秒)", data.getAvgHandleTime(), compareData.getAvgHandleTime());
            addCompareSummaryItem(summary, "超时警情数", data.getTimeoutCount(), compareData.getTimeoutCount());

            writer.write(summary, true);

            writer.setCurrentRow(writer.getCurrentRow() + 2);
            writer.merge(1, "处置质量统计");
            writer.setCurrentRow(writer.getCurrentRow() + 1);
            writer.addHeaderAlias("item", "指标");
            writer.addHeaderAlias("value", "数值");
            writer.write(data.getHandleStats(), true);

            writer.setCurrentRow(writer.getCurrentRow() + 2);
            writer.merge(1, "学校处置排名");
            writer.setCurrentRow(writer.getCurrentRow() + 1);
            writer.addHeaderAlias("schoolName", "学校名称");
            writer.addHeaderAlias("handleRate", "处置完成率");
            writer.addHeaderAlias("avgResponseTime", "平均响应时间");
            writer.addHeaderAlias("rankChange", "排名变化");
            writer.addHeaderAlias("riskSummary", "高风险原因摘要");

            List<Map<String, Object>> schoolHandleStats = new ArrayList<>();
            for (Map<String, Object> cd : compareDataMap.values()) {
                Map<String, Object> stat = new LinkedHashMap<>();
                stat.put("schoolName", cd.get("schoolName"));
                stat.put("handleRate", cd.get("currentHandleRate") + "%");
                stat.put("avgResponseTime", cd.get("currentAvgResponseTime"));
                stat.put("rankChange", formatRankChange((Integer) cd.get("rankChange")));
                stat.put("riskSummary", cd.get("riskSummary"));
                schoolHandleStats.add(stat);
            }
            writer.write(schoolHandleStats, true);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writer.flush(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("导出处置统计报表失败", e);
            throw BusinessException.of(ResultCode.EXPORT_FAILED);
        }
    }

    private byte[] exportTrendReport(ReportStatisticsDTO data, String fileName, LocalDate startDate, LocalDate endDate, Long schoolId, Long groupId) {
        try (ExcelWriter writer = ExcelUtil.getWriter()) {
            writer.addHeaderAlias("item", "统计项");
            writer.addHeaderAlias("value", "数值");
            writer.addHeaderAlias("compareValue", "上期数值");
            writer.addHeaderAlias("changeRate", "环比变化");

            String currentPeriod = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            String comparePeriod = startDate.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));
            Map<String, Map<String, Object>> compareDataMap = getCompareDataMap(currentPeriod, comparePeriod, schoolId, groupId);
            ReportStatisticsDTO compareData = getTrendStatistics(startDate.minusMonths(1), endDate.minusMonths(1), schoolId, groupId);

            List<Map<String, Object>> summary = new ArrayList<>();
            addSummaryItem(summary, "统计周期", startDate + " 至 " + endDate);
            addCompareSummaryItem(summary, "总报警数", data.getTotalAlarms(), compareData.getTotalAlarms());

            writer.write(summary, true);

            writer.setCurrentRow(writer.getCurrentRow() + 2);
            writer.merge(1, "每日报警趋势");
            writer.setCurrentRow(writer.getCurrentRow() + 1);
            writer.addHeaderAlias("date", "日期");
            writer.addHeaderAlias("count", "报警数量");
            writer.addHeaderAlias("compareCount", "上期数量");
            writer.addHeaderAlias("changeRate", "环比变化");

            List<Map<String, Object>> enhancedTrendStats = new ArrayList<>();
            Map<String, Long> compareDateCount = compareData.getTrendStats().stream()
                    .collect(Collectors.toMap(
                            m -> (String) m.get("date"),
                            m -> (Long) m.get("count"),
                            (a, b) -> a
                    ));

            for (Map<String, Object> stat : data.getTrendStats()) {
                Map<String, Object> enhancedStat = new LinkedHashMap<>(stat);
                String date = (String) stat.get("date");
                Long currentCount = (Long) stat.get("count");
                String compareDate = LocalDate.parse(date).minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                Long compareCount = compareDateCount.getOrDefault(compareDate, 0L);
                enhancedStat.put("compareCount", compareCount);
                if (compareCount != null && compareCount > 0) {
                    BigDecimal changeRate = BigDecimal.valueOf(currentCount - compareCount)
                            .multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(compareCount), 2, RoundingMode.HALF_UP);
                    enhancedStat.put("changeRate", changeRate + "%");
                } else {
                    enhancedStat.put("changeRate", "-");
                }
                enhancedTrendStats.add(enhancedStat);
            }
            writer.write(enhancedTrendStats, true);

            writer.setCurrentRow(writer.getCurrentRow() + 2);
            writer.merge(1, "学校趋势对比");
            writer.setCurrentRow(writer.getCurrentRow() + 1);
            writer.addHeaderAlias("schoolName", "学校名称");
            writer.addHeaderAlias("currentAlarmCount", "本期警情数");
            writer.addHeaderAlias("compareAlarmCount", "上期警情数");
            writer.addHeaderAlias("rankChange", "排名变化");
            writer.addHeaderAlias("riskSummary", "高风险原因摘要");

            List<Map<String, Object>> schoolCompareStats = new ArrayList<>();
            for (Map<String, Object> cd : compareDataMap.values()) {
                Map<String, Object> stat = new LinkedHashMap<>();
                stat.put("schoolName", cd.get("schoolName"));
                stat.put("currentAlarmCount", cd.get("currentAlarmCount"));
                stat.put("compareAlarmCount", cd.get("compareAlarmCount"));
                stat.put("rankChange", formatRankChange((Integer) cd.get("rankChange")));
                stat.put("riskSummary", cd.get("riskSummary"));
                schoolCompareStats.add(stat);
            }
            writer.write(schoolCompareStats, true);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writer.flush(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("导出趋势分析报表失败", e);
            throw new RuntimeException("导出报表失败", e);
        }
    }

    private void addSummaryItem(List<Map<String, Object>> list, String item, Object value) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("item", item);
        map.put("value", value);
        list.add(map);
    }

    private void addCompareSummaryItem(List<Map<String, Object>> list, String item, Object currentValue, Object compareValue) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("item", item);
        map.put("value", currentValue);
        map.put("compareValue", compareValue);
        if (compareValue != null && !compareValue.equals(0L) && !compareValue.equals(BigDecimal.ZERO)) {
            BigDecimal current = currentValue instanceof Number ?
                    BigDecimal.valueOf(((Number) currentValue).doubleValue()) :
                    (BigDecimal) currentValue;
            BigDecimal compare = compareValue instanceof Number ?
                    BigDecimal.valueOf(((Number) compareValue).doubleValue()) :
                    (BigDecimal) compareValue;
            if (compare.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal changeRate = current.subtract(compare)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(compare, 2, RoundingMode.HALF_UP);
                map.put("changeRate", changeRate + "%");
            } else {
                map.put("changeRate", "-");
            }
        } else {
            map.put("changeRate", "-");
        }
        list.add(map);
    }

    @Override
    public CompareResultVO getSchoolCompare(CompareQueryDTO queryDTO) {
        String currentPeriod = queryDTO.getCurrentPeriod();
        String comparePeriod = queryDTO.getComparePeriod();
        String compareType = queryDTO.getCompareType();

        String[] currentDateRange = parsePeriodToDateRange(currentPeriod, compareType);
        String[] compareDateRange = parsePeriodToDateRange(comparePeriod, compareType);

        List<Map<String, Object>> currentAlarmStats = reportMapper.selectAlarmStatsByPeriod(
                currentDateRange[0], currentDateRange[1],
                queryDTO.getSchoolId(), queryDTO.getGroupId(), queryDTO.getTownId()
        );

        List<Map<String, Object>> compareAlarmStats = reportMapper.selectAlarmStatsByPeriod(
                compareDateRange[0], compareDateRange[1],
                queryDTO.getSchoolId(), queryDTO.getGroupId(), queryDTO.getTownId()
        );

        List<Map<String, Object>> currentRankStats = reportMapper.selectRankByPeriod(
                currentDateRange[0], currentDateRange[1],
                queryDTO.getSchoolId(), queryDTO.getGroupId(), queryDTO.getTownId()
        );

        List<Map<String, Object>> compareRankStats = reportMapper.selectRankByPeriod(
                compareDateRange[0], compareDateRange[1],
                queryDTO.getSchoolId(), queryDTO.getGroupId(), queryDTO.getTownId()
        );

        Map<Long, SchoolCompareItemVO> currentMap = buildCompareItemMap(currentAlarmStats, currentRankStats);
        Map<Long, SchoolCompareItemVO> compareMap = buildCompareItemMap(compareAlarmStats, compareRankStats);

        List<SchoolCompareItemVO> schoolCompareList = mergeCompareItems(currentMap, compareMap);

        CompareResultVO result = new CompareResultVO();
        result.setCurrentPeriod(currentPeriod);
        result.setComparePeriod(comparePeriod);
        result.setCompareType(compareType);
        result.setSchoolCompareList(schoolCompareList);
        result.setOverallSummary(buildOverallSummary(schoolCompareList, currentPeriod, comparePeriod));

        return result;
    }

    @Override
    public CompareResultVO getMonthlyCompare(String month1, String month2, Long schoolId, Long groupId) {
        CompareQueryDTO queryDTO = new CompareQueryDTO();
        queryDTO.setCompareType("month");
        queryDTO.setSchoolId(schoolId);
        queryDTO.setGroupId(groupId);
        queryDTO.setCurrentPeriod(month1);
        queryDTO.setComparePeriod(month2);
        return getSchoolCompare(queryDTO);
    }

    @Override
    public CompareResultVO getQuarterlyCompare(String quarter1, String quarter2, Long schoolId, Long groupId) {
        CompareQueryDTO queryDTO = new CompareQueryDTO();
        queryDTO.setCompareType("quarter");
        queryDTO.setSchoolId(schoolId);
        queryDTO.setGroupId(groupId);
        queryDTO.setCurrentPeriod(quarter1);
        queryDTO.setComparePeriod(quarter2);
        return getSchoolCompare(queryDTO);
    }

    @Override
    public byte[] exportCompareReport(CompareQueryDTO queryDTO) {
        CompareResultVO compareResult = getSchoolCompare(queryDTO);

        try (ExcelWriter writer = ExcelUtil.getWriter()) {
            writer.addHeaderAlias("schoolName", "学校名称");
            writer.addHeaderAlias("currentAlarmCount", "本期警情数");
            writer.addHeaderAlias("compareAlarmCount", "上期警情数");
            writer.addHeaderAlias("alarmChangeRate", "警情变化率");
            writer.addHeaderAlias("alarmChangeTrend", "警情趋势");
            writer.addHeaderAlias("currentAvgResponseTime", "本期平均响应时间");
            writer.addHeaderAlias("compareAvgResponseTime", "上期平均响应时间");
            writer.addHeaderAlias("responseChangeRate", "响应时间变化率");
            writer.addHeaderAlias("responseChangeTrend", "响应时间趋势");
            writer.addHeaderAlias("currentHandleRate", "本期处置完成率");
            writer.addHeaderAlias("compareHandleRate", "上期处置完成率");
            writer.addHeaderAlias("handleChangeRate", "处置率变化率");
            writer.addHeaderAlias("handleChangeTrend", "处置率趋势");
            writer.addHeaderAlias("currentTimeoutCount", "本期超时次数");
            writer.addHeaderAlias("compareTimeoutCount", "上期超时次数");
            writer.addHeaderAlias("timeoutChangeRate", "超时变化率");
            writer.addHeaderAlias("timeoutChangeTrend", "超时趋势");
            writer.addHeaderAlias("currentRank", "本期排名");
            writer.addHeaderAlias("compareRank", "上期排名");
            writer.addHeaderAlias("rankChange", "排名变化");
            writer.addHeaderAlias("riskSummary", "高风险原因摘要");

            List<Map<String, Object>> summary = new ArrayList<>();
            addSummaryItem(summary, "对比类型", "month".equals(queryDTO.getCompareType()) ? "月度对比" : "季度对比");
            addSummaryItem(summary, "当前期", compareResult.getCurrentPeriod());
            addSummaryItem(summary, "对比期", compareResult.getComparePeriod());
            addSummaryItem(summary, "整体汇总", compareResult.getOverallSummary());

            writer.write(summary, true);

            writer.setCurrentRow(writer.getCurrentRow() + 2);
            writer.merge(1, "学校对比详情");
            writer.setCurrentRow(writer.getCurrentRow() + 1);

            List<Map<String, Object>> exportData = new ArrayList<>();
            for (SchoolCompareItemVO item : compareResult.getSchoolCompareList()) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("schoolName", item.getSchoolName());
                map.put("currentAlarmCount", item.getCurrentAlarmCount());
                map.put("compareAlarmCount", item.getCompareAlarmCount());
                map.put("currentCriticalAlarmCount", item.getCurrentCriticalAlarmCount());
                map.put("compareCriticalAlarmCount", item.getCompareCriticalAlarmCount());
                map.put("alarmChangeRate", item.getAlarmChangeRate() + "%");
                map.put("alarmChangeTrend", formatTrend(item.getAlarmChangeTrend()));
                map.put("currentAvgResponseTime", item.getCurrentAvgResponseTime());
                map.put("compareAvgResponseTime", item.getCompareAvgResponseTime());
                map.put("responseChangeRate", item.getResponseChangeRate() + "%");
                map.put("responseChangeTrend", formatTrend(item.getResponseChangeTrend()));
                map.put("currentHandleRate", item.getCurrentHandleRate() + "%");
                map.put("compareHandleRate", item.getCompareHandleRate() + "%");
                map.put("handleChangeRate", item.getHandleChangeRate() + "%");
                map.put("handleChangeTrend", formatTrend(item.getHandleChangeTrend()));
                map.put("currentTimeoutCount", item.getCurrentTimeoutCount());
                map.put("compareTimeoutCount", item.getCompareTimeoutCount());
                map.put("timeoutChangeRate", item.getTimeoutChangeRate() + "%");
                map.put("timeoutChangeTrend", formatTrend(item.getTimeoutChangeTrend()));
                map.put("currentRank", item.getCurrentRank());
                map.put("compareRank", item.getCompareRank());
                map.put("rankChange", formatRankChange(item.getRankChange()));
                map.put("riskSummary", item.getRiskSummary());
                exportData.add(map);
            }

            writer.write(exportData, true);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writer.flush(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("导出对比报表失败", e);
            throw new RuntimeException("导出报表失败", e);
        }
    }

    private String[] parsePeriodToDateRange(String period, String compareType) {
        if ("month".equals(compareType)) {
            YearMonth yearMonth = YearMonth.parse(period);
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.atEndOfMonth();
            return new String[]{
                    startDate.atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    endDate.atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            };
        } else if ("quarter".equals(compareType)) {
            String[] parts = period.split("-Q");
            int year = Integer.parseInt(parts[0]);
            int quarter = Integer.parseInt(parts[1]);
            int startMonth = (quarter - 1) * 3 + 1;
            int endMonth = quarter * 3;
            LocalDate startDate = LocalDate.of(year, startMonth, 1);
            LocalDate endDate = YearMonth.of(year, endMonth).atEndOfMonth();
            return new String[]{
                    startDate.atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    endDate.atTime(23, 59, 59).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            };
        }
        throw BusinessException.of(ResultCode.PARAM_VALIDATE_ERROR, "不支持的对比类型：" + compareType);
    }

    private Map<Long, SchoolCompareItemVO> buildCompareItemMap(List<Map<String, Object>> alarmStats, List<Map<String, Object>> rankStats) {
        Map<Long, SchoolCompareItemVO> map = new HashMap<>();
        Map<Long, Map<String, Object>> rankMap = rankStats.stream()
                .collect(Collectors.toMap(
                        m -> ((Number) m.get("school_id")).longValue(),
                        m -> m
                ));

        for (Map<String, Object> stat : alarmStats) {
            Long schoolId = ((Number) stat.get("school_id")).longValue();
            SchoolCompareItemVO item = new SchoolCompareItemVO();
            item.setSchoolId(schoolId);
            item.setSchoolName((String) stat.get("school_name"));
            item.setSchoolType((Integer) stat.get("school_type"));
            item.setCurrentAlarmCount(((Number) stat.get("alarm_count")).longValue());
            item.setCurrentAvgResponseTime(stat.get("avg_response_time") != null ?
                    BigDecimal.valueOf(((Number) stat.get("avg_response_time")).doubleValue()) :
                    BigDecimal.ZERO);

            Long handledCount = stat.get("handled_count") != null ?
                    ((Number) stat.get("handled_count")).longValue() : 0L;
            Long criticalCount = stat.get("critical_alarm_count") != null ?
                    ((Number) stat.get("critical_alarm_count")).longValue() : 0L;
            Long totalCount = item.getCurrentAlarmCount();
            item.setCurrentCriticalAlarmCount(criticalCount);
            item.setCurrentHandleRate(totalCount > 0 ?
                    BigDecimal.valueOf(handledCount)
                            .multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP) :
                    BigDecimal.ZERO);

            Map<String, Object> rankStat = rankMap.get(schoolId);
            if (rankStat != null) {
                item.setCurrentTimeoutCount(rankStat.get("timeout_count") != null ?
                        ((Number) rankStat.get("timeout_count")).longValue() : 0L);
                item.setCurrentRank(((Number) rankStat.get("rank_num")).intValue());
            } else {
                item.setCurrentTimeoutCount(0L);
                item.setCurrentRank(0);
            }

            map.put(schoolId, item);
        }

        return map;
    }

    private List<SchoolCompareItemVO> mergeCompareItems(Map<Long, SchoolCompareItemVO> currentMap, Map<Long, SchoolCompareItemVO> compareMap) {
        List<SchoolCompareItemVO> result = new ArrayList<>();

        for (Map.Entry<Long, SchoolCompareItemVO> entry : currentMap.entrySet()) {
            Long schoolId = entry.getKey();
            SchoolCompareItemVO current = entry.getValue();
            SchoolCompareItemVO compare = compareMap.get(schoolId);

            if (compare == null) {
                compare = new SchoolCompareItemVO();
                compare.setCompareAlarmCount(0L);
                compare.setCompareCriticalAlarmCount(0L);
                compare.setCompareAvgResponseTime(BigDecimal.ZERO);
                compare.setCompareHandleRate(BigDecimal.ZERO);
                compare.setCompareTimeoutCount(0L);
                compare.setCompareRank(currentMap.size() + 1);
            }

            current.setCompareAlarmCount(compare.getCurrentAlarmCount());
            current.setCompareCriticalAlarmCount(compare.getCurrentCriticalAlarmCount());
            current.setCompareAvgResponseTime(compare.getCurrentAvgResponseTime());
            current.setCompareHandleRate(compare.getCurrentHandleRate());
            current.setCompareTimeoutCount(compare.getCurrentTimeoutCount());
            current.setCompareRank(compare.getCurrentRank());

            current.setAlarmChangeRate(calculateChangeRate(current.getCurrentAlarmCount(), compare.getCurrentAlarmCount()));
            current.setAlarmChangeTrend(determineTrend(current.getAlarmChangeRate()));

            current.setResponseChangeRate(calculateChangeRate(current.getCurrentAvgResponseTime(), compare.getCurrentAvgResponseTime()));
            current.setResponseChangeTrend(determineTrend(current.getResponseChangeRate()));

            current.setHandleChangeRate(calculateChangeRate(current.getCurrentHandleRate(), compare.getCurrentHandleRate()));
            current.setHandleChangeTrend(determineTrend(current.getHandleChangeRate()));

            current.setTimeoutChangeRate(calculateChangeRate(current.getCurrentTimeoutCount(), compare.getCurrentTimeoutCount()));
            current.setTimeoutChangeTrend(determineTrend(current.getTimeoutChangeRate()));

            current.setRankChange(compare.getCurrentRank() - current.getCurrentRank());

            current.setRiskSummary(generateRiskSummary(current));

            result.add(current);
        }

        result.sort(Comparator.comparing(SchoolCompareItemVO::getCurrentRank));
        return result;
    }

    private BigDecimal calculateChangeRate(Number current, Number compare) {
        BigDecimal currentBD = current != null ?
                BigDecimal.valueOf(current.doubleValue()) : BigDecimal.ZERO;
        BigDecimal compareBD = compare != null ?
                BigDecimal.valueOf(compare.doubleValue()) : BigDecimal.ZERO;

        if (compareBD.compareTo(BigDecimal.ZERO) == 0) {
            return currentBD.compareTo(BigDecimal.ZERO) > 0 ?
                    BigDecimal.valueOf(100) : BigDecimal.ZERO;
        }

        return currentBD.subtract(compareBD)
                .multiply(BigDecimal.valueOf(100))
                .divide(compareBD, 2, RoundingMode.HALF_UP);
    }

    private String determineTrend(BigDecimal changeRate) {
        if (changeRate == null) {
            return "flat";
        }
        int comparison = changeRate.compareTo(BigDecimal.ZERO);
        if (comparison > 0) {
            return "up";
        } else if (comparison < 0) {
            return "down";
        } else {
            return "flat";
        }
    }

    private String generateRiskSummary(SchoolCompareItemVO item) {
        List<String> reasons = new ArrayList<>();

        if (item.getCurrentCriticalAlarmCount() != null && item.getCurrentCriticalAlarmCount() > 0) {
            reasons.add("重大警情" + item.getCurrentCriticalAlarmCount() + "起");
        }

        if (item.getCurrentAvgResponseTime() != null && item.getCurrentAvgResponseTime().compareTo(BigDecimal.valueOf(120)) > 0) {
            reasons.add("平均响应超时" + item.getCurrentAvgResponseTime().intValue() + "秒");
        }

        if (item.getCurrentTimeoutCount() != null && item.getCurrentTimeoutCount() > 3) {
            reasons.add("超时响应" + item.getCurrentTimeoutCount() + "次");
        }

        if (item.getCurrentHandleRate() != null && item.getCurrentHandleRate().compareTo(BigDecimal.valueOf(80)) < 0) {
            reasons.add("处置完成率" + item.getCurrentHandleRate().setScale(0, RoundingMode.HALF_UP) + "%");
        }

        return reasons.isEmpty() ? "正常" : String.join("；", reasons);
    }

    private String buildOverallSummary(List<SchoolCompareItemVO> list, String currentPeriod, String comparePeriod) {
        long upCount = list.stream().filter(s -> "up".equals(s.getAlarmChangeTrend())).count();
        long downCount = list.stream().filter(s -> "down".equals(s.getAlarmChangeTrend())).count();
        long flatCount = list.stream().filter(s -> "flat".equals(s.getAlarmChangeTrend())).count();
        long highRiskCount = list.stream().filter(s -> !"正常".equals(s.getRiskSummary())).count();

        return String.format("本次对比%s与%s，共%d所学校，警情上升%d所，下降%d所，持平%d所，高风险学校%d所",
                currentPeriod, comparePeriod, list.size(), upCount, downCount, flatCount, highRiskCount);
    }

    private String formatTrend(String trend) {
        if ("up".equals(trend)) {
            return "上升";
        } else if ("down".equals(trend)) {
            return "下降";
        } else {
            return "持平";
        }
    }

    private String formatRankChange(Integer rankChange) {
        if (rankChange == null) {
            return "-";
        }
        if (rankChange > 0) {
            return "上升" + rankChange + "位↑";
        } else if (rankChange < 0) {
            return "下降" + Math.abs(rankChange) + "位↓";
        } else {
            return "持平";
        }
    }

    private Map<String, Map<String, Object>> getCompareDataMap(String currentPeriod, String comparePeriod, Long schoolId, Long groupId) {
        CompareQueryDTO queryDTO = new CompareQueryDTO();
        queryDTO.setCompareType("month");
        queryDTO.setSchoolId(schoolId);
        queryDTO.setGroupId(groupId);
        queryDTO.setCurrentPeriod(currentPeriod);
        queryDTO.setComparePeriod(comparePeriod);

        CompareResultVO result = getSchoolCompare(queryDTO);

        Map<String, Map<String, Object>> dataMap = new HashMap<>();
        for (SchoolCompareItemVO item : result.getSchoolCompareList()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("schoolId", item.getSchoolId());
            map.put("schoolName", item.getSchoolName());
            map.put("currentAlarmCount", item.getCurrentAlarmCount());
            map.put("compareAlarmCount", item.getCompareAlarmCount());
            map.put("currentAvgResponseTime", item.getCurrentAvgResponseTime());
            map.put("compareAvgResponseTime", item.getCompareAvgResponseTime());
            map.put("currentHandleRate", item.getCurrentHandleRate());
            map.put("compareHandleRate", item.getCompareHandleRate());
            map.put("currentTimeoutCount", item.getCurrentTimeoutCount());
            map.put("compareTimeoutCount", item.getCompareTimeoutCount());
            map.put("rankChange", item.getRankChange());
            map.put("riskSummary", item.getRiskSummary());
            dataMap.put(String.valueOf(item.getSchoolId()), map);
        }
        return dataMap;
    }

    private void validateDutyReviewQuery(DutyReviewQueryDTO queryDTO) {
        if (queryDTO.getStartDate() == null || queryDTO.getEndDate() == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "startDate, endDate");
        }
        if (queryDTO.getStartDate().isAfter(queryDTO.getEndDate())) {
            throw BusinessException.of(ResultCode.PARAM_RANGE_ERROR, "开始日期不能晚于结束日期");
        }
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(queryDTO.getStartDate(), queryDTO.getEndDate());
        if (daysBetween > 90) {
            throw BusinessException.of(ResultCode.PARAM_RANGE_ERROR, "日期范围不能超过90天");
        }
    }

    private String getShiftName(Integer shiftType) {
        if (shiftType == null) return "";
        return shiftType == 1 ? "白班" : "夜班";
    }

    private BigDecimal calcRate(BigDecimal dividend, BigDecimal divisor) {
        if (divisor == null || divisor.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return dividend.multiply(BigDecimal.valueOf(100)).divide(divisor, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal getBigDecimal(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val == null) return BigDecimal.ZERO;
        if (val instanceof BigDecimal) return (BigDecimal) val;
        if (val instanceof Number) return BigDecimal.valueOf(((Number) val).doubleValue());
        try {
            return new BigDecimal(val.toString());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private Long getLong(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val == null) return 0L;
        if (val instanceof Number) return ((Number) val).longValue();
        try {
            return Long.parseLong(val.toString());
        } catch (Exception e) {
            return 0L;
        }
    }

    private BigDecimal calculateRankScore(DutyReviewShiftVO vo) {
        BigDecimal score = BigDecimal.ZERO;

        long extraAlarms = Math.max(0, vo.getTotalAlarmCount() - 10);
        BigDecimal alarmDeduct = BigDecimal.valueOf(extraAlarms / 5);
        BigDecimal alarmScore = BigDecimal.valueOf(20).min(BigDecimal.valueOf(20).subtract(alarmDeduct));
        score = score.add(alarmScore.max(BigDecimal.ZERO));

        BigDecimal timeoutDeduct = vo.getResponseTimeoutRate() != null ?
                vo.getResponseTimeoutRate().multiply(BigDecimal.valueOf(2)) : BigDecimal.ZERO;
        BigDecimal timeoutScore = BigDecimal.valueOf(30).subtract(timeoutDeduct);
        score = score.add(timeoutScore.max(BigDecimal.ZERO));

        BigDecimal remindDeduct = BigDecimal.valueOf(vo.getRemindCount() * 2);
        BigDecimal remindScore = BigDecimal.valueOf(20).subtract(remindDeduct);
        score = score.add(remindScore.max(BigDecimal.ZERO));

        BigDecimal policeDeduct = vo.getPoliceFeedbackRate() != null ?
                BigDecimal.valueOf(100).subtract(vo.getPoliceFeedbackRate()) : BigDecimal.valueOf(100);
        BigDecimal policeScore = BigDecimal.valueOf(20).subtract(policeDeduct);
        score = score.add(policeScore.max(BigDecimal.ZERO));

        BigDecimal completionDeduct = vo.getCompletionRate() != null ?
                BigDecimal.valueOf(100).subtract(vo.getCompletionRate()).multiply(BigDecimal.valueOf(0.5)) : BigDecimal.valueOf(50);
        BigDecimal completionScore = BigDecimal.valueOf(10).subtract(completionDeduct);
        score = score.add(completionScore.max(BigDecimal.ZERO));

        return score.setScale(2, RoundingMode.HALF_UP);
    }

    private String generateSchoolRiskSummary(DutyReviewSchoolVO vo) {
        List<String> reasons = new ArrayList<>();

        if (vo.getCriticalAlarmCount() != null && vo.getCriticalAlarmCount() > 0) {
            reasons.add("重大警情" + vo.getCriticalAlarmCount() + "起");
        }

        if (vo.getResponseTimeoutRate() != null && vo.getResponseTimeoutRate().compareTo(BigDecimal.valueOf(10)) > 0) {
            reasons.add("响应超时率" + vo.getResponseTimeoutRate().setScale(1, RoundingMode.HALF_UP) + "%");
        }

        if (vo.getAvgResponseTime() != null && vo.getAvgResponseTime().compareTo(BigDecimal.valueOf(300)) > 0) {
            reasons.add("平均响应超时" + vo.getAvgResponseTime().intValue() + "秒");
        }

        if (vo.getRemindCount() != null && vo.getRemindCount() > 2) {
            reasons.add("催办" + vo.getRemindCount() + "次");
        }

        if (vo.getCompletionRate() != null && vo.getCompletionRate().compareTo(BigDecimal.valueOf(80)) < 0) {
            reasons.add("处置完成率" + vo.getCompletionRate().setScale(1, RoundingMode.HALF_UP) + "%");
        }

        return reasons.isEmpty() ? "正常" : String.join("；", reasons);
    }

    @Override
    public DutyReviewSummaryVO getDutyReviewSummary(DutyReviewQueryDTO queryDTO) {
        validateDutyReviewQuery(queryDTO);

        DutyReviewSummaryVO summary = new DutyReviewSummaryVO();
        summary.setQueryStartDate(queryDTO.getStartDate());
        summary.setQueryEndDate(queryDTO.getEndDate());

        List<DutyReviewShiftVO> shiftList = getDutyReviewByShift(queryDTO);
        List<DutyReviewSchoolVO> schoolList = getDutyReviewBySchool(queryDTO);

        summary.setShiftReviewList(shiftList);
        summary.setSchoolReviewList(schoolList);

        summary.setTotalShiftCount((long) shiftList.size());
        summary.setTotalAlarmCount(shiftList.stream()
                .map(DutyReviewShiftVO::getTotalAlarmCount)
                .filter(Objects::nonNull)
                .reduce(0L, Long::sum));
        summary.setTotalCriticalCount(shiftList.stream()
                .map(DutyReviewShiftVO::getCriticalAlarmCount)
                .filter(Objects::nonNull)
                .reduce(0L, Long::sum));
        summary.setTotalTimeoutCount(shiftList.stream()
                .map(DutyReviewShiftVO::getTimeoutResponseCount)
                .filter(Objects::nonNull)
                .reduce(0L, Long::sum));

        BigDecimal totalAlarm = BigDecimal.valueOf(summary.getTotalAlarmCount());
        BigDecimal totalTimeout = BigDecimal.valueOf(summary.getTotalTimeoutCount());
        summary.setOverallResponseRate(calcRate(totalTimeout, totalAlarm));

        BigDecimal totalRespTime = shiftList.stream()
                .filter(s -> s.getAvgResponseTime() != null && s.getTotalAlarmCount() != null && s.getTotalAlarmCount() > 0)
                .map(s -> s.getAvgResponseTime().multiply(BigDecimal.valueOf(s.getTotalAlarmCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setOverallResponseAvgTime(totalAlarm.compareTo(BigDecimal.ZERO) > 0 ?
                totalRespTime.divide(totalAlarm, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        BigDecimal fbRateSum = shiftList.stream()
                .filter(s -> s.getPoliceFeedbackRate() != null && s.getPoliceFeedbackCount() != null && s.getPoliceFeedbackCount() > 0)
                .map(s -> s.getPoliceFeedbackRate().multiply(BigDecimal.valueOf(s.getPoliceFeedbackCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Long totalFb = shiftList.stream()
                .map(DutyReviewShiftVO::getPoliceFeedbackCount)
                .filter(Objects::nonNull)
                .reduce(0L, Long::sum);
        summary.setOverallPoliceFeedbackRate(totalFb > 0 ?
                fbRateSum.divide(BigDecimal.valueOf(totalFb), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        BigDecimal compSum = shiftList.stream()
                .filter(s -> s.getCompletionRate() != null && s.getTotalAlarmCount() != null && s.getTotalAlarmCount() > 0)
                .map(s -> s.getCompletionRate().multiply(BigDecimal.valueOf(s.getTotalAlarmCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setOverallCompletionRate(totalAlarm.compareTo(BigDecimal.ZERO) > 0 ?
                compSum.divide(totalAlarm, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        List<DutyReviewSchoolVO> top5 = schoolList.stream()
                .sorted(Comparator.comparing(DutyReviewSchoolVO::getCriticalAlarmCount, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(DutyReviewSchoolVO::getTotalAlarmCount, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(DutyReviewSchoolVO::getResponseTimeoutRate, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .collect(Collectors.toList());
        summary.setTop5HighRiskSchools(top5);

        return summary;
    }

    @Override
    public List<DutyReviewShiftVO> getDutyReviewByShift(DutyReviewQueryDTO queryDTO) {
        validateDutyReviewQuery(queryDTO);

        List<Map<String, Object>> shiftDataList = reportMapper.selectShiftReview(
                queryDTO.getStartDate(),
                queryDTO.getEndDate(),
                queryDTO.getDutyShift(),
                queryDTO.getUserId(),
                queryDTO.getTownId(),
                queryDTO.getGroupId(),
                queryDTO.getSchoolType()
        );

        List<DutyReviewShiftVO> result = new ArrayList<>();
        for (Map<String, Object> data : shiftDataList) {
            DutyReviewShiftVO vo = new DutyReviewShiftVO();

            java.sql.Date dd = (java.sql.Date) data.get("duty_date");
            if (dd != null) {
                vo.setDutyDate(dd.toLocalDate());
            }
            vo.setDutyShiftId(getLong(data, "duty_shift_id"));
            vo.setDutyShiftName(getShiftName((Integer) data.get("shift_type")));
            vo.setDutyUserId(getLong(data, "duty_user_id"));
            vo.setDutyUserName((String) data.get("duty_user_name"));
            vo.setTotalAlarmCount(getLong(data, "total_alarm_count"));
            vo.setCriticalAlarmCount(getLong(data, "critical_alarm_count"));
            vo.setTimeoutResponseCount(getLong(data, "timeout_response_count"));
            vo.setResponseTimeoutRate(calcRate(
                    BigDecimal.valueOf(vo.getTimeoutResponseCount()),
                    BigDecimal.valueOf(vo.getTotalAlarmCount())
            ));
            vo.setAvgResponseTime(getBigDecimal(data, "avg_response_time").setScale(2, RoundingMode.HALF_UP));
            vo.setAvgHandleTime(getBigDecimal(data, "avg_handle_time").setScale(2, RoundingMode.HALF_UP));
            vo.setCloseCount(getLong(data, "close_count"));
            vo.setCarryCount(getLong(data, "carry_count"));

            Long handledCount = getLong(data, "handled_count");
            vo.setCompletionRate(calcRate(
                    BigDecimal.valueOf(handledCount),
                    BigDecimal.valueOf(vo.getTotalAlarmCount())
            ));

            java.time.LocalDate dutyDate = vo.getDutyDate();
            Integer shiftType = (Integer) data.get("shift_type");

            if (dutyDate != null && shiftType != null) {
                String startTime, endTime;
                if (shiftType == 1) {
                    startTime = dutyDate + " 08:00:00";
                    endTime = dutyDate + " 20:00:00";
                } else {
                    startTime = dutyDate + " 20:00:00";
                    endTime = dutyDate.plusDays(1) + " 08:00:00";
                }

                Long remindCount = reportMapper.selectRemindCountByRange(
                        startTime, endTime,
                        queryDTO.getTownId(),
                        queryDTO.getGroupId(),
                        queryDTO.getSchoolType()
                );
                vo.setRemindCount(remindCount != null ? remindCount : 0L);

                List<Map<String, Object>> fbStats = reportMapper.selectPoliceFeedbackStats(
                        startTime, endTime,
                        queryDTO.getTownId(),
                        queryDTO.getGroupId(),
                        queryDTO.getSchoolType()
                );
                if (!fbStats.isEmpty()) {
                    Map<String, Object> fb = fbStats.get(0);
                    Long totalFb = getLong(fb, "total_feedback");
                    Long inTimeFb = getLong(fb, "in_time_feedback");
                    vo.setPoliceFeedbackCount(totalFb);
                    vo.setPoliceFeedbackInTimeCount(inTimeFb);
                    vo.setPoliceFeedbackRate(calcRate(
                            BigDecimal.valueOf(inTimeFb),
                            BigDecimal.valueOf(totalFb)
                    ));
                    vo.setPoliceFeedbackAvgTime(getBigDecimal(fb, "avg_feedback_time").setScale(2, RoundingMode.HALF_UP));
                } else {
                    vo.setPoliceFeedbackCount(0L);
                    vo.setPoliceFeedbackInTimeCount(0L);
                    vo.setPoliceFeedbackRate(BigDecimal.ZERO);
                    vo.setPoliceFeedbackAvgTime(BigDecimal.ZERO);
                }

                List<Map<String, Object>> riskEvents = reportMapper.selectRiskEventList(
                        dutyDate, shiftType,
                        queryDTO.getTownId(),
                        queryDTO.getGroupId(),
                        queryDTO.getSchoolType()
                );
                List<Map<String, Object>> riskEventList = new ArrayList<>();
                for (Map<String, Object> re : riskEvents) {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("alarmNo", re.get("alarm_no"));
                    item.put("schoolName", re.get("school_name"));
                    item.put("levelName", com.safetycampus.alarm.enums.AlarmLevelEnum.getDescByCode((Integer) re.get("alarm_level")));
                    item.put("handleSummary", re.get("handle_summary"));
                    riskEventList.add(item);
                }
                vo.setRiskEvents(riskEventList);
            }

            vo.setRankScore(calculateRankScore(vo));
            result.add(vo);
        }

        return result;
    }

    @Override
    public List<DutyReviewSchoolVO> getDutyReviewBySchool(DutyReviewQueryDTO queryDTO) {
        validateDutyReviewQuery(queryDTO);

        List<Map<String, Object>> schoolDataList = reportMapper.selectSchoolReview(
                queryDTO.getStartDate(),
                queryDTO.getEndDate(),
                queryDTO.getDutyShift(),
                queryDTO.getUserId(),
                queryDTO.getTownId(),
                queryDTO.getGroupId(),
                queryDTO.getSchoolType()
        );

        String startTime = queryDTO.getStartDate() + " 00:00:00";
        String endTime = queryDTO.getEndDate() + " 23:59:59";
        List<Map<String, Object>> fbStats = reportMapper.selectPoliceFeedbackStats(
                startTime, endTime,
                queryDTO.getTownId(),
                queryDTO.getGroupId(),
                queryDTO.getSchoolType()
        );
        BigDecimal overallFbRate = BigDecimal.ZERO;
        if (!fbStats.isEmpty()) {
            Map<String, Object> fb = fbStats.get(0);
            Long totalFb = getLong(fb, "total_feedback");
            Long inTimeFb = getLong(fb, "in_time_feedback");
            overallFbRate = calcRate(BigDecimal.valueOf(inTimeFb), BigDecimal.valueOf(totalFb));
        }

        Long totalRemind = reportMapper.selectRemindCountByRange(
                startTime, endTime,
                queryDTO.getTownId(),
                queryDTO.getGroupId(),
                queryDTO.getSchoolType()
        );
        long remindTotal = totalRemind != null ? totalRemind : 0L;

        List<DutyReviewSchoolVO> result = new ArrayList<>();
        for (Map<String, Object> data : schoolDataList) {
            DutyReviewSchoolVO vo = new DutyReviewSchoolVO();

            vo.setSchoolId(getLong(data, "school_id"));
            vo.setSchoolName((String) data.get("school_name"));
            vo.setSchoolTypeName(com.safetycampus.school.enums.SchoolTypeEnum.getDescByCode((Integer) data.get("school_type")));
            vo.setTotalAlarmCount(getLong(data, "total_alarm_count"));
            vo.setCriticalAlarmCount(getLong(data, "critical_alarm_count"));
            vo.setTimeoutResponseCount(getLong(data, "timeout_response_count"));
            vo.setResponseTimeoutRate(calcRate(
                    BigDecimal.valueOf(vo.getTimeoutResponseCount()),
                    BigDecimal.valueOf(vo.getTotalAlarmCount())
            ));
            vo.setAvgResponseTime(getBigDecimal(data, "avg_response_time").setScale(2, RoundingMode.HALF_UP));
            vo.setAvgHandleTime(getBigDecimal(data, "avg_handle_time").setScale(2, RoundingMode.HALF_UP));

            long totalAll = getLong(data, "total_alarm_count");
            vo.setPoliceFeedbackRate(totalAll > 0 ? overallFbRate : BigDecimal.ZERO);
            vo.setRemindCount(totalAll > 0 && remindTotal > 0 ?
                    (long) Math.ceil((double) (totalAll * remindTotal) / Math.max(1, totalAll)) : 0L);

            Long handledCount = getLong(data, "handled_count");
            vo.setCompletionRate(calcRate(
                    BigDecimal.valueOf(handledCount),
                    BigDecimal.valueOf(vo.getTotalAlarmCount())
            ));

            vo.setRiskSummary(generateSchoolRiskSummary(vo));
            result.add(vo);
        }

        return result;
    }

    @Override
    public byte[] exportDutyReview(DutyReviewQueryDTO queryDTO) {
        validateDutyReviewQuery(queryDTO);

        DutyReviewSummaryVO summary = getDutyReviewSummary(queryDTO);

        try (ExcelWriter writer = ExcelUtil.getWriter(true)) {
            writer.renameSheet(0, "班次复盘汇总");
            List<Map<String, Object>> shiftExport = new ArrayList<>();
            for (DutyReviewShiftVO s : summary.getShiftReviewList()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("值班日期", s.getDutyDate());
                row.put("班次", s.getDutyShiftName());
                row.put("值班人员", s.getDutyUserName());
                row.put("总接警量", s.getTotalAlarmCount());
                row.put("重大警情数", s.getCriticalAlarmCount());
                row.put("响应超时数", s.getTimeoutResponseCount());
                row.put("响应超时率(%)", s.getResponseTimeoutRate());
                row.put("平均响应时间(秒)", s.getAvgResponseTime());
                row.put("催办次数", s.getRemindCount());
                row.put("派出所反馈率(%)", s.getPoliceFeedbackRate());
                row.put("平均处置时长(秒)", s.getAvgHandleTime());
                row.put("处置完成率(%)", s.getCompletionRate());
                row.put("已关闭警情数", s.getCloseCount());
                row.put("遗留未处置数", s.getCarryCount());
                row.put("班次评分", s.getRankScore());
                shiftExport.add(row);
            }
            writer.write(shiftExport, true);

            writer.setSheet("学校维度汇总");
            List<Map<String, Object>> schoolExport = new ArrayList<>();
            for (DutyReviewSchoolVO sc : summary.getSchoolReviewList()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("学校名称", sc.getSchoolName());
                row.put("学校类型", sc.getSchoolTypeName());
                row.put("总接警量", sc.getTotalAlarmCount());
                row.put("重大警情数", sc.getCriticalAlarmCount());
                row.put("响应超时数", sc.getTimeoutResponseCount());
                row.put("响应超时率(%)", sc.getResponseTimeoutRate());
                row.put("平均响应时间(秒)", sc.getAvgResponseTime());
                row.put("平均处置时长(秒)", sc.getAvgHandleTime());
                row.put("催办次数", sc.getRemindCount());
                row.put("派出所反馈率(%)", sc.getPoliceFeedbackRate());
                row.put("处置完成率(%)", sc.getCompletionRate());
                row.put("风险摘要", sc.getRiskSummary());
                schoolExport.add(row);
            }
            writer.write(schoolExport, true);

            writer.setSheet("TOP5高风险学校");
            List<Map<String, Object>> top5Export = new ArrayList<>();
            int rank = 1;
            for (DutyReviewSchoolVO sc : summary.getTop5HighRiskSchools()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("排名", rank++);
                row.put("学校名称", sc.getSchoolName());
                row.put("学校类型", sc.getSchoolTypeName());
                row.put("总接警量", sc.getTotalAlarmCount());
                row.put("重大警情数", sc.getCriticalAlarmCount());
                row.put("响应超时率(%)", sc.getResponseTimeoutRate());
                row.put("平均响应时间(秒)", sc.getAvgResponseTime());
                row.put("处置完成率(%)", sc.getCompletionRate());
                row.put("风险摘要", sc.getRiskSummary());
                top5Export.add(row);
            }
            writer.write(top5Export, true);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writer.flush(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("导出值守复盘报表失败", e);
            throw BusinessException.of(ResultCode.EXPORT_FAILED);
        }
    }
}

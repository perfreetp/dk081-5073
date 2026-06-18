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
import com.safetycampus.report.dto.ReportStatisticsDTO;
import com.safetycampus.report.service.ReportService;
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
                return exportAlarmReport(data, fileName, startDate, endDate);
            case "handle":
                data = getHandleStatistics(startDate, endDate, schoolId, groupId);
                fileName = "处置统计报表_" + startDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + endDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
                return exportHandleReport(data, fileName, startDate, endDate);
            case "trend":
                data = getTrendStatistics(startDate, endDate, schoolId, groupId);
                fileName = "趋势分析报表_" + startDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + endDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
                return exportTrendReport(data, fileName, startDate, endDate);
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

    private byte[] exportAlarmReport(ReportStatisticsDTO data, String fileName, LocalDate startDate, LocalDate endDate) {
        try (ExcelWriter writer = ExcelUtil.getWriter()) {
            writer.addHeaderAlias("item", "统计项");
            writer.addHeaderAlias("value", "数值");

            List<Map<String, Object>> summary = new ArrayList<>();
            addSummaryItem(summary, "统计周期", startDate + " 至 " + endDate);
            addSummaryItem(summary, "总报警数", data.getTotalAlarms());
            addSummaryItem(summary, "待处置警情", data.getPendingAlarms());
            addSummaryItem(summary, "已处置警情", data.getHandledAlarms());
            addSummaryItem(summary, "已关闭警情", data.getClosedAlarms());
            addSummaryItem(summary, "重大警情", data.getCriticalAlarms());
            addSummaryItem(summary, "超时警情", data.getTimeoutCount());

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
            writer.write(data.getSchoolStats(), true);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writer.flush(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("导出警情统计报表失败", e);
            throw new RuntimeException("导出报表失败", e);
        }
    }

    private byte[] exportHandleReport(ReportStatisticsDTO data, String fileName, LocalDate startDate, LocalDate endDate) {
        try (ExcelWriter writer = ExcelUtil.getWriter()) {
            writer.addHeaderAlias("item", "统计项");
            writer.addHeaderAlias("value", "数值");

            List<Map<String, Object>> summary = new ArrayList<>();
            addSummaryItem(summary, "统计周期", startDate + " 至 " + endDate);
            addSummaryItem(summary, "总报警数", data.getTotalAlarms());
            addSummaryItem(summary, "已处置警情", data.getHandledAlarms());
            addSummaryItem(summary, "已关闭警情", data.getClosedAlarms());
            addSummaryItem(summary, "平均响应时间(秒)", data.getAvgResponseTime());
            addSummaryItem(summary, "平均处置时间(秒)", data.getAvgHandleTime());
            addSummaryItem(summary, "超时警情数", data.getTimeoutCount());

            writer.write(summary, true);

            writer.setCurrentRow(writer.getCurrentRow() + 2);
            writer.merge(1, "处置质量统计");
            writer.setCurrentRow(writer.getCurrentRow() + 1);
            writer.addHeaderAlias("item", "指标");
            writer.addHeaderAlias("value", "数值");
            writer.write(data.getHandleStats(), true);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writer.flush(out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("导出处置统计报表失败", e);
            throw new RuntimeException("导出报表失败", e);
        }
    }

    private byte[] exportTrendReport(ReportStatisticsDTO data, String fileName, LocalDate startDate, LocalDate endDate) {
        try (ExcelWriter writer = ExcelUtil.getWriter()) {
            writer.addHeaderAlias("item", "统计项");
            writer.addHeaderAlias("value", "数值");

            List<Map<String, Object>> summary = new ArrayList<>();
            addSummaryItem(summary, "统计周期", startDate + " 至 " + endDate);
            addSummaryItem(summary, "总报警数", data.getTotalAlarms());

            writer.write(summary, true);

            writer.setCurrentRow(writer.getCurrentRow() + 2);
            writer.merge(1, "每日报警趋势");
            writer.setCurrentRow(writer.getCurrentRow() + 1);
            writer.addHeaderAlias("date", "日期");
            writer.addHeaderAlias("count", "报警数量");
            writer.write(data.getTrendStats(), true);

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
}

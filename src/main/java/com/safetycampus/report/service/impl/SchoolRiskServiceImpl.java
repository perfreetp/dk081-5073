package com.safetycampus.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetycampus.alarm.entity.AlarmRecord;
import com.safetycampus.alarm.enums.AlarmLevelEnum;
import com.safetycampus.alarm.mapper.AlarmRecordMapper;
import com.safetycampus.common.exception.BusinessException;
import com.safetycampus.report.dto.RiskQueryDTO;
import com.safetycampus.report.entity.SchoolRisk;
import com.safetycampus.report.enums.RiskLevelEnum;
import com.safetycampus.report.mapper.SchoolRiskMapper;
import com.safetycampus.report.service.SchoolRiskService;
import com.safetycampus.school.entity.SchoolInfo;
import com.safetycampus.school.mapper.SchoolInfoMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SchoolRiskServiceImpl extends ServiceImpl<SchoolRiskMapper, SchoolRisk> implements SchoolRiskService {

    @Resource
    private AlarmRecordMapper alarmRecordMapper;

    @Resource
    private SchoolInfoMapper schoolInfoMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public BigDecimal calculateRiskScore(Long schoolId, String statMonth) {
        YearMonth yearMonth = YearMonth.parse(statMonth);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(23, 59, 59);

        LambdaQueryWrapper<AlarmRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmRecord::getSchoolId, schoolId);
        wrapper.ge(AlarmRecord::getCreatedAt, startTime);
        wrapper.le(AlarmRecord::getCreatedAt, endTime);
        wrapper.eq(AlarmRecord::getDeleted, 0);
        List<AlarmRecord> records = alarmRecordMapper.selectList(wrapper);

        int totalAlarms = records.size();
        int criticalAlarms = 0;
        int totalResponseTime = 0;
        int responseCount = 0;
        int timeoutCount = 0;

        for (AlarmRecord record : records) {
            if (AlarmLevelEnum.CRITICAL.getCode().equals(record.getAlarmLevel())) {
                criticalAlarms++;
            }
            if (record.getFirstResponseAt() != null) {
                long seconds = java.time.Duration.between(record.getCreatedAt(), record.getFirstResponseAt()).getSeconds();
                totalResponseTime += seconds;
                responseCount++;
                int timeoutSeconds = AlarmLevelEnum.CRITICAL.getCode().equals(record.getAlarmLevel()) ? 300 :
                                    AlarmLevelEnum.MAJOR.getCode().equals(record.getAlarmLevel()) ? 600 : 1800;
                if (seconds > timeoutSeconds) {
                    timeoutCount++;
                }
            }
        }

        int avgResponseTime = responseCount > 0 ? totalResponseTime / responseCount : 0;

        BigDecimal totalAlarmScore = calculateDimensionScore(totalAlarms, 100, 30);
        BigDecimal criticalAlarmScore = calculateDimensionScore(criticalAlarms, 10, 25);
        BigDecimal responseTimeScore = calculateResponseTimeScore(avgResponseTime, 20);
        BigDecimal timeoutScore = calculateDimensionScore(timeoutCount, 10, 25);

        BigDecimal totalScore = totalAlarmScore.add(criticalAlarmScore).add(responseTimeScore).add(timeoutScore);

        return totalScore.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SchoolRisk generateRiskPortrait(Long schoolId, String statMonth) {
        SchoolInfo school = schoolInfoMapper.selectById(schoolId);
        if (school == null) {
            throw new BusinessException("学校不存在");
        }

        YearMonth yearMonth = YearMonth.parse(statMonth);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(23, 59, 59);

        LambdaQueryWrapper<AlarmRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmRecord::getSchoolId, schoolId);
        wrapper.ge(AlarmRecord::getCreatedAt, startTime);
        wrapper.le(AlarmRecord::getCreatedAt, endTime);
        wrapper.eq(AlarmRecord::getDeleted, 0);
        List<AlarmRecord> records = alarmRecordMapper.selectList(wrapper);

        int totalAlarms = records.size();
        int criticalAlarms = 0;
        int majorAlarms = 0;
        int generalAlarms = 0;
        int totalResponseTime = 0;
        int totalHandleTime = 0;
        int responseCount = 0;
        int handleCount = 0;
        int timeoutCount = 0;

        for (AlarmRecord record : records) {
            if (AlarmLevelEnum.CRITICAL.getCode().equals(record.getAlarmLevel())) {
                criticalAlarms++;
            } else if (AlarmLevelEnum.MAJOR.getCode().equals(record.getAlarmLevel())) {
                majorAlarms++;
            } else {
                generalAlarms++;
            }
            if (record.getFirstResponseAt() != null) {
                long seconds = java.time.Duration.between(record.getCreatedAt(), record.getFirstResponseAt()).getSeconds();
                totalResponseTime += seconds;
                responseCount++;
                int timeoutSeconds = AlarmLevelEnum.CRITICAL.getCode().equals(record.getAlarmLevel()) ? 300 :
                                    AlarmLevelEnum.MAJOR.getCode().equals(record.getAlarmLevel()) ? 600 : 1800;
                if (seconds > timeoutSeconds) {
                    timeoutCount++;
                }
            }
            if (record.getHandledAt() != null && record.getFirstResponseAt() != null) {
                long seconds = java.time.Duration.between(record.getFirstResponseAt(), record.getHandledAt()).getSeconds();
                totalHandleTime += seconds;
                handleCount++;
            }
        }

        int avgResponseTime = responseCount > 0 ? totalResponseTime / responseCount : 0;
        int avgHandleTime = handleCount > 0 ? totalHandleTime / handleCount : 0;

        BigDecimal riskScore = calculateRiskScore(schoolId, statMonth);
        Integer riskLevel = determineRiskLevel(riskScore);

        Map<String, Object> indicators = new HashMap<>();
        indicators.put("totalAlarms", totalAlarms);
        indicators.put("criticalAlarms", criticalAlarms);
        indicators.put("majorAlarms", majorAlarms);
        indicators.put("generalAlarms", generalAlarms);
        indicators.put("avgResponseTime", avgResponseTime);
        indicators.put("avgHandleTime", avgHandleTime);
        indicators.put("timeoutCount", timeoutCount);
        indicators.put("responseRate", responseCount > 0 && totalAlarms > 0 ? 
            BigDecimal.valueOf(responseCount).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(totalAlarms), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        String indicatorsJson;
        try {
            indicatorsJson = objectMapper.writeValueAsString(indicators);
        } catch (JsonProcessingException e) {
            log.error("序列化指标数据失败", e);
            indicatorsJson = "{}";
        }

        LambdaQueryWrapper<SchoolRisk> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(SchoolRisk::getSchoolId, schoolId);
        existWrapper.eq(SchoolRisk::getStatMonth, statMonth);
        SchoolRisk existRisk = baseMapper.selectOne(existWrapper);

        SchoolRisk risk;
        if (existRisk != null) {
            risk = existRisk;
        } else {
            risk = new SchoolRisk();
            risk.setSchoolId(schoolId);
            risk.setStatMonth(statMonth);
        }
        risk.setTotalAlarms(totalAlarms);
        risk.setCriticalAlarms(criticalAlarms);
        risk.setAvgResponseTime(avgResponseTime);
        risk.setAvgHandleTime(avgHandleTime);
        risk.setTimeoutCount(timeoutCount);
        risk.setSchoolLevel(school.getSchoolLevel());
        risk.setRiskScore(riskScore);
        risk.setRiskLevel(riskLevel);
        risk.setIndicators(indicatorsJson);
        risk.setStatDate(LocalDate.now());

        if (existRisk != null) {
            updateById(risk);
        } else {
            save(risk);
        }

        return risk;
    }

    @Override
    public List<SchoolRisk> getHighRiskSchools(String statMonth) {
        return baseMapper.selectHighRiskSchools(RiskLevelEnum.HIGH.getCode(), statMonth);
    }

    @Override
    public IPage<SchoolRisk> selectPage(RiskQueryDTO queryDTO) {
        return baseMapper.selectPageByCondition(queryDTO.buildPage(), queryDTO);
    }

    @Override
    public SchoolRisk getDetail(Long id) {
        return getById(id);
    }

    private BigDecimal calculateDimensionScore(int value, int maxValue, int weight) {
        if (value <= 0) {
            return BigDecimal.valueOf(weight);
        }
        if (value >= maxValue) {
            return BigDecimal.ZERO;
        }
        double score = weight * (1 - (double) value / maxValue);
        return BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateResponseTimeScore(int avgResponseTime, int weight) {
        if (avgResponseTime <= 60) {
            return BigDecimal.valueOf(weight);
        }
        if (avgResponseTime >= 600) {
            return BigDecimal.ZERO;
        }
        double score = weight * (1 - (double) (avgResponseTime - 60) / 540);
        return BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP);
    }

    private Integer determineRiskLevel(BigDecimal riskScore) {
        if (riskScore.compareTo(BigDecimal.valueOf(60)) < 0) {
            return RiskLevelEnum.HIGH.getCode();
        } else if (riskScore.compareTo(BigDecimal.valueOf(80)) < 0) {
            return RiskLevelEnum.MEDIUM.getCode();
        } else {
            return RiskLevelEnum.LOW.getCode();
        }
    }
}

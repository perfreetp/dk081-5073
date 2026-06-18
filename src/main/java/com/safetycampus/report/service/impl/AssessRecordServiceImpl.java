package com.safetycampus.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.safetycampus.alarm.entity.AlarmRecord;
import com.safetycampus.alarm.enums.AlarmLevelEnum;
import com.safetycampus.alarm.enums.AlarmStatusEnum;
import com.safetycampus.alarm.mapper.AlarmRecordMapper;
import com.safetycampus.common.exception.BusinessException;
import com.safetycampus.common.result.ResultCode;
import com.safetycampus.report.dto.AssessQueryDTO;
import com.safetycampus.report.entity.AssessRecord;
import com.safetycampus.report.enums.AssessGradeEnum;
import com.safetycampus.report.mapper.AssessRecordMapper;
import com.safetycampus.report.service.AssessRecordService;
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
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class AssessRecordServiceImpl extends ServiceImpl<AssessRecordMapper, AssessRecord> implements AssessRecordService {

    @Resource
    private AlarmRecordMapper alarmRecordMapper;

    @Resource
    private SchoolInfoMapper schoolInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssessRecord calculateQuarterAssess(Long schoolId, String statQuarter) {
        SchoolInfo school = schoolInfoMapper.selectById(schoolId);
        if (school == null) {
            throw BusinessException.of(ResultCode.SCHOOL_NOT_FOUND);
        }

        LocalDate[] quarterRange = parseQuarter(statQuarter);
        LocalDateTime startTime = quarterRange[0].atStartOfDay();
        LocalDateTime endTime = quarterRange[1].atTime(23, 59, 59);

        LambdaQueryWrapper<AlarmRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmRecord::getSchoolId, schoolId);
        wrapper.ge(AlarmRecord::getCreatedAt, startTime);
        wrapper.le(AlarmRecord::getCreatedAt, endTime);
        wrapper.eq(AlarmRecord::getDeleted, 0);
        List<AlarmRecord> records = alarmRecordMapper.selectList(wrapper);

        BigDecimal alarmScore = calculateAlarmScore(records);
        BigDecimal responseScore = calculateResponseScore(records);
        BigDecimal handleScore = calculateHandleScore(records);
        BigDecimal dutyScore = calculateDutyScore(schoolId, startTime, endTime);

        BigDecimal totalScore = alarmScore.add(responseScore).add(handleScore).add(dutyScore)
                .setScale(2, RoundingMode.HALF_UP);

        String grade = determineGrade(totalScore);

        LambdaQueryWrapper<AssessRecord> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(AssessRecord::getSchoolId, schoolId);
        existWrapper.eq(AssessRecord::getStatQuarter, statQuarter);
        AssessRecord existRecord = baseMapper.selectOne(existWrapper);

        AssessRecord record;
        if (existRecord != null) {
            record = existRecord;
        } else {
            record = new AssessRecord();
            record.setSchoolId(schoolId);
            record.setStatQuarter(statQuarter);
        }
        record.setTotalScore(totalScore);
        record.setAlarmScore(alarmScore);
        record.setResponseScore(responseScore);
        record.setHandleScore(handleScore);
        record.setDutyScore(dutyScore);
        record.setGrade(grade);
        record.setAssessDate(LocalDate.now());

        if (existRecord != null) {
            updateById(record);
        } else {
            save(record);
        }

        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean generateRank(String statQuarter) {
        List<SchoolInfo> schools = schoolInfoMapper.selectList(
                new LambdaQueryWrapper<SchoolInfo>().eq(SchoolInfo::getStatus, 1));

        for (SchoolInfo school : schools) {
            calculateQuarterAssess(school.getId(), statQuarter);
        }

        LambdaQueryWrapper<AssessRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssessRecord::getStatQuarter, statQuarter);
        List<AssessRecord> records = baseMapper.selectList(wrapper);

        records.sort(Comparator.comparing(AssessRecord::getTotalScore).reversed());

        for (int i = 0; i < records.size(); i++) {
            AssessRecord record = records.get(i);
            record.setRankNum(i + 1);
            updateById(record);
        }

        log.info("季度考核排名生成完成，季度：{}，共{}所学校", statQuarter, records.size());
        return true;
    }

    @Override
    public List<AssessRecord> getQuarterRank(String statQuarter) {
        return baseMapper.selectQuarterRank(statQuarter);
    }

    @Override
    public IPage<AssessRecord> selectPage(AssessQueryDTO queryDTO) {
        return baseMapper.selectPageByCondition(queryDTO.buildPage(), queryDTO);
    }

    @Override
    public AssessRecord getDetail(Long id) {
        return getById(id);
    }

    private BigDecimal calculateAlarmScore(List<AlarmRecord> records) {
        if (records.isEmpty()) {
            return BigDecimal.valueOf(25);
        }

        int totalAlarms = records.size();
        int criticalAlarms = 0;
        int majorAlarms = 0;

        for (AlarmRecord record : records) {
            if (AlarmLevelEnum.CRITICAL.getCode().equals(record.getAlarmLevel())) {
                criticalAlarms++;
            } else if (AlarmLevelEnum.MAJOR.getCode().equals(record.getAlarmLevel())) {
                majorAlarms++;
            }
        }

        double score = 25;
        score -= criticalAlarms * 3;
        score -= majorAlarms * 1;
        score -= Math.max(0, totalAlarms - 10) * 0.1;

        return BigDecimal.valueOf(Math.max(0, score)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateResponseScore(List<AlarmRecord> records) {
        if (records.isEmpty()) {
            return BigDecimal.valueOf(25);
        }

        int totalResponseTime = 0;
        int responseCount = 0;
        int timeoutCount = 0;

        for (AlarmRecord record : records) {
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

        if (responseCount == 0) {
            return BigDecimal.ZERO;
        }

        int avgResponseTime = totalResponseTime / responseCount;
        double score = 25;

        if (avgResponseTime <= 60) {
            score = 25;
        } else if (avgResponseTime <= 180) {
            score = 22;
        } else if (avgResponseTime <= 300) {
            score = 18;
        } else if (avgResponseTime <= 600) {
            score = 14;
        } else {
            score = 10;
        }

        score -= timeoutCount * 2;

        return BigDecimal.valueOf(Math.max(0, score)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateHandleScore(List<AlarmRecord> records) {
        if (records.isEmpty()) {
            return BigDecimal.valueOf(25);
        }

        int handledCount = 0;
        int totalHandleTime = 0;
        int handleCount = 0;
        int closedCount = 0;

        for (AlarmRecord record : records) {
            if (AlarmStatusEnum.HANDLED.getCode().equals(record.getStatus()) ||
                AlarmStatusEnum.CLOSED.getCode().equals(record.getStatus())) {
                handledCount++;
            }
            if (record.getHandledAt() != null && record.getFirstResponseAt() != null) {
                long seconds = java.time.Duration.between(record.getFirstResponseAt(), record.getHandledAt()).getSeconds();
                totalHandleTime += seconds;
                handleCount++;
            }
            if (AlarmStatusEnum.CLOSED.getCode().equals(record.getStatus())) {
                closedCount++;
            }
        }

        double handleRate = records.size() > 0 ? (double) handledCount / records.size() : 0;
        double closeRate = records.size() > 0 ? (double) closedCount / records.size() : 0;
        int avgHandleTime = handleCount > 0 ? totalHandleTime / handleCount : 0;

        double score = 25;
        score = score * handleRate * 0.4 + score * closeRate * 0.3;

        if (avgHandleTime > 0) {
            if (avgHandleTime <= 1800) {
                score += 7.5;
            } else if (avgHandleTime <= 3600) {
                score += 5;
            } else if (avgHandleTime <= 7200) {
                score += 2.5;
            }
        }

        return BigDecimal.valueOf(Math.min(25, Math.max(0, score))).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDutyScore(Long schoolId, LocalDateTime startTime, LocalDateTime endTime) {
        return BigDecimal.valueOf(25);
    }

    private String determineGrade(BigDecimal totalScore) {
        if (totalScore.compareTo(BigDecimal.valueOf(90)) >= 0) {
            return AssessGradeEnum.A.getCode();
        } else if (totalScore.compareTo(BigDecimal.valueOf(75)) >= 0) {
            return AssessGradeEnum.B.getCode();
        } else if (totalScore.compareTo(BigDecimal.valueOf(60)) >= 0) {
            return AssessGradeEnum.C.getCode();
        } else {
            return AssessGradeEnum.D.getCode();
        }
    }

    private LocalDate[] parseQuarter(String statQuarter) {
        String[] parts = statQuarter.split("-Q");
        int year = Integer.parseInt(parts[0]);
        int quarter = Integer.parseInt(parts[1]);

        int startMonth = (quarter - 1) * 3 + 1;
        int endMonth = quarter * 3;

        LocalDate startDate = YearMonth.of(year, startMonth).atDay(1);
        LocalDate endDate = YearMonth.of(year, endMonth).atEndOfMonth();

        return new LocalDate[]{startDate, endDate};
    }
}

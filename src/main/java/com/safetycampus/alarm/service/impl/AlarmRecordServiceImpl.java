package com.safetycampus.alarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.safetycampus.alarm.dto.AlarmMergeDTO;
import com.safetycampus.alarm.dto.AlarmQueryDTO;
import com.safetycampus.alarm.dto.AlarmReceiveDTO;
import com.safetycampus.alarm.entity.AlarmFlow;
import com.safetycampus.alarm.entity.AlarmRecord;
import com.safetycampus.alarm.entity.AlarmRemind;
import com.safetycampus.alarm.entity.HolidayConfig;
import com.safetycampus.alarm.enums.AlarmLevelEnum;
import com.safetycampus.alarm.enums.AlarmStatusEnum;
import com.safetycampus.alarm.mapper.AlarmRecordMapper;
import com.safetycampus.alarm.service.AlarmFlowService;
import com.safetycampus.alarm.service.AlarmRecordService;
import com.safetycampus.alarm.mapper.HolidayConfigMapper;
import com.safetycampus.common.exception.BusinessException;
import com.safetycampus.common.result.ResultCode;
import com.safetycampus.plan.service.AlarmPlanService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AlarmRecordServiceImpl extends ServiceImpl<AlarmRecordMapper, AlarmRecord> implements AlarmRecordService {

    @Resource
    private HolidayConfigMapper holidayConfigMapper;

    @Resource
    private AlarmFlowService alarmFlowService;

    @Resource
    private com.safetycampus.alarm.mapper.AlarmRemindMapper alarmRemindMapper;

    @Resource
    private AlarmPlanService alarmPlanService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AlarmRecord receiveAlarm(AlarmReceiveDTO dto) {
        boolean isHoliday = checkHolidayMode();

        AlarmRecord record = new AlarmRecord();
        record.setAlarmNo(generateAlarmNo());
        record.setSchoolId(dto.getSchoolId());
        record.setDeviceId(dto.getSchoolId());
        record.setAlarmType(dto.getAlarmType());
        record.setAlarmLevel(dto.getAlarmLevel());
        record.setAlarmTitle(dto.getAlarmTitle());
        record.setAlarmContent(dto.getAlarmContent());
        record.setReporterName(dto.getReporterName());
        record.setReporterPhone(dto.getReporterPhone());
        record.setLocation(dto.getLocation());
        record.setLongitude(dto.getLongitude());
        record.setLatitude(dto.getLatitude());
        record.setParentId(0L);
        record.setMergedCount(1);
        record.setStatus(AlarmStatusEnum.PENDING.getCode());
        record.setIsEscalated(0);
        record.setIsHoliday(isHoliday ? 1 : 0);
        save(record);

        AlarmFlow flow = new AlarmFlow();
        flow.setAlarmId(record.getId());
        flow.setFlowType(1);
        flow.setOperatorName("系统");
        flow.setRemark("报警接收成功");
        alarmFlowService.save(flow);

        createAlarmRemind(record.getId(), dto.getAlarmLevel());

        try {
            alarmPlanService.matchAndLinkPlan(record.getId());
        } catch (Exception e) {
            log.error("警情预案匹配失败，alarmId: {}", record.getId(), e);
        }

        if (AlarmLevelEnum.CRITICAL.getCode().equals(dto.getAlarmLevel())) {
            escalateCriticalAlarm(record.getId(), null, "系统");
        }

        return record;
    }

    @Override
    public IPage<AlarmRecord> selectPage(AlarmQueryDTO queryDTO) {
        return baseMapper.selectPageByCondition(queryDTO.buildPage(), queryDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean mergeDuplicateAlarms(AlarmMergeDTO mergeDTO) {
        AlarmRecord parent = getById(mergeDTO.getParentId());
        if (parent == null) {
            throw new BusinessException("主警情不存在");
        }

        List<Long> alarmIds = mergeDTO.getAlarmIds();
        if (alarmIds.contains(mergeDTO.getParentId())) {
            alarmIds.remove(mergeDTO.getParentId());
        }

        List<AlarmRecord> children = listByIds(alarmIds);
        if (children.isEmpty()) {
            throw new BusinessException("未找到可合并的警情");
        }

        for (AlarmRecord child : children) {
            child.setParentId(mergeDTO.getParentId());
            child.setStatus(AlarmStatusEnum.CLOSED.getCode());
            child.setClosedAt(LocalDateTime.now());
        }
        updateBatchById(children);

        parent.setMergedCount(parent.getMergedCount() + children.size());
        updateById(parent);

        AlarmFlow flow = new AlarmFlow();
        flow.setAlarmId(mergeDTO.getParentId());
        flow.setFlowType(2);
        flow.setOperatorId(mergeDTO.getOperatorId());
        flow.setOperatorName(mergeDTO.getOperatorName());
        flow.setRemark("合并警情：" + alarmIds + "，" + mergeDTO.getRemark());
        alarmFlowService.save(flow);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean escalateCriticalAlarm(Long alarmId, Long operatorId, String operatorName) {
        AlarmRecord record = getById(alarmId);
        if (record == null) {
            throw BusinessException.of(ResultCode.ALARM_NOT_FOUND);
        }
        if (record.getIsEscalated() == 1) {
            throw BusinessException.of(ResultCode.ALARM_STATUS_NOT_ALLOWED);
        }

        record.setIsEscalated(1);
        record.setEscalatedAt(LocalDateTime.now());
        updateById(record);

        AlarmFlow flow = new AlarmFlow();
        flow.setAlarmId(alarmId);
        flow.setFlowType(3);
        flow.setOperatorId(operatorId);
        flow.setOperatorName(operatorName);
        flow.setRemark("重大警情已上推至教育局");
        alarmFlowService.save(flow);

        return true;
    }

    @Override
    public boolean checkHolidayMode() {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<HolidayConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HolidayConfig::getHolidayDate, today);
        return holidayConfigMapper.selectCount(wrapper) > 0;
    }

    @Override
    public AlarmRecord getDetail(Long id) {
        return getById(id);
    }

    private String generateAlarmNo() {
        String dateStr = LocalDate.now().toString().replace("-", "");
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "AL" + dateStr + uuid;
    }

    private void createAlarmRemind(Long alarmId, Integer alarmLevel) {
        LocalDateTime now = LocalDateTime.now();
        int timeoutMinutes = AlarmLevelEnum.CRITICAL.getCode().equals(alarmLevel) ? 5 :
                             AlarmLevelEnum.MAJOR.getCode().equals(alarmLevel) ? 10 : 30;

        AlarmRemind remind = new AlarmRemind();
        remind.setAlarmId(alarmId);
        remind.setRemindType(1);
        remind.setRemindCount(0);
        remind.setNextRemindAt(now.plusMinutes(timeoutMinutes));
        remind.setStatus(1);
        alarmRemindMapper.insert(remind);
    }
}

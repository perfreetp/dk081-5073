package com.safetycampus.alarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.safetycampus.alarm.entity.AlarmFlow;
import com.safetycampus.alarm.mapper.AlarmFlowMapper;
import com.safetycampus.alarm.service.AlarmFlowService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlarmFlowServiceImpl extends ServiceImpl<AlarmFlowMapper, AlarmFlow> implements AlarmFlowService {

    @Override
    public List<AlarmFlow> getFlowByAlarmId(Long alarmId) {
        LambdaQueryWrapper<AlarmFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmFlow::getAlarmId, alarmId);
        wrapper.orderByAsc(AlarmFlow::getCreatedAt);
        return list(wrapper);
    }

    @Override
    public void addFlowRecord(Long alarmId, Integer flowType, String remark) {
        addFlowRecord(alarmId, flowType, null, null, null, remark, null);
    }

    @Override
    public void addFlowRecord(Long alarmId, Integer flowType, Long operatorId, String operatorName,
                              String operatorRole, String remark) {
        addFlowRecord(alarmId, flowType, operatorId, operatorName, operatorRole, remark, null);
    }

    @Override
    public void addFlowRecord(Long alarmId, Integer flowType, Long operatorId, String operatorName,
                              String operatorRole, String remark, String attachUrl) {
        AlarmFlow flow = new AlarmFlow();
        flow.setAlarmId(alarmId);
        flow.setFlowType(flowType);
        flow.setOperatorId(operatorId);
        flow.setOperatorName(operatorName);
        flow.setOperatorRole(operatorRole);
        flow.setRemark(remark);
        flow.setAttachUrl(attachUrl);
        flow.setCreatedAt(LocalDateTime.now());
        save(flow);
    }
}

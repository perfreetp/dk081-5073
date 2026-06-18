package com.safetycampus.alarm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.safetycampus.alarm.entity.AlarmFlow;

import java.util.List;

public interface AlarmFlowService extends IService<AlarmFlow> {

    List<AlarmFlow> getFlowByAlarmId(Long alarmId);

    void addFlowRecord(Long alarmId, Integer flowType, String remark);

    void addFlowRecord(Long alarmId, Integer flowType, Long operatorId, String operatorName,
                       String operatorRole, String remark);

    void addFlowRecord(Long alarmId, Integer flowType, Long operatorId, String operatorName,
                       String operatorRole, String remark, String attachUrl);
}

package com.safetycampus.alarm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.safetycampus.alarm.dto.AlarmTimelineQueryDTO;
import com.safetycampus.alarm.dto.AlarmTimelineSummaryVO;
import com.safetycampus.alarm.dto.AlarmTimelineVO;
import com.safetycampus.alarm.entity.AlarmFlow;

import java.util.List;

public interface AlarmFlowService extends IService<AlarmFlow> {

    List<AlarmFlow> getFlowByAlarmId(Long alarmId);

    void addFlowRecord(Long alarmId, Integer flowType, String remark);

    void addFlowRecord(Long alarmId, Integer flowType, Long operatorId, String operatorName,
                       String operatorRole, String remark);

    void addFlowRecord(Long alarmId, Integer flowType, Long operatorId, String operatorName,
                       String operatorRole, String remark, String attachUrl);

    void addFlowRecord(Long alarmId, Integer flowType, Long operatorId, String operatorName,
                       String operatorRole, Integer partyType, Long partyId, String partyName,
                       String remark, String attachUrl);

    List<AlarmTimelineVO> getAlarmTimeline(AlarmTimelineQueryDTO queryDTO);

    AlarmTimelineSummaryVO getTimelineSummary(Long alarmId);
}

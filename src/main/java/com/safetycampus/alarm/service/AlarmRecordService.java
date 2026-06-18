package com.safetycampus.alarm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.safetycampus.alarm.dto.AlarmMergeDTO;
import com.safetycampus.alarm.dto.AlarmQueryDTO;
import com.safetycampus.alarm.dto.AlarmReceiveDTO;
import com.safetycampus.alarm.entity.AlarmRecord;

public interface AlarmRecordService extends IService<AlarmRecord> {

    AlarmRecord receiveAlarm(AlarmReceiveDTO dto);

    IPage<AlarmRecord> selectPage(AlarmQueryDTO queryDTO);

    boolean mergeDuplicateAlarms(AlarmMergeDTO mergeDTO);

    boolean escalateCriticalAlarm(Long alarmId, Long operatorId, String operatorName);

    boolean checkHolidayMode();

    AlarmRecord getDetail(Long id);
}

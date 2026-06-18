package com.safetycampus.notify.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.safetycampus.common.entity.PageQuery;
import com.safetycampus.notify.dto.NotifySendDTO;
import com.safetycampus.notify.entity.NotifyRecord;

import java.util.List;

public interface NotifyService extends IService<NotifyRecord> {

    boolean sendSms(String phone, String name, String title, String content, String templateCode, Long alarmId);

    boolean sendAppPush(String target, String name, String title, String content, Long alarmId);

    boolean sendCall(String phone, String name, String title, String content, Long alarmId);

    boolean batchNotify(NotifySendDTO dto);

    boolean notifyPoliceStation(Long policeStationId, Long alarmId, String alarmTitle, String alarmContent);

    NotifyRecord createNotifyRecord(Long alarmId, Integer notifyType, String notifyTarget, String targetName,
                                    String title, String content, String templateCode);

    IPage<NotifyRecord> selectPage(PageQuery query, Long alarmId, Integer notifyType, Integer status);

    List<NotifyRecord> listByAlarmId(Long alarmId);

    boolean markAsRead(Long id);

    boolean resend(Long id);
}

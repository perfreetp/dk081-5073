package com.safetycampus.notify.listener;

import com.safetycampus.alarm.entity.AlarmRecord;
import com.safetycampus.alarm.enums.AlarmLevelEnum;
import com.safetycampus.notify.entity.ContactBook;
import com.safetycampus.notify.entity.PoliceStation;
import com.safetycampus.notify.enums.UnitTypeEnum;
import com.safetycampus.notify.service.ContactBookService;
import com.safetycampus.notify.service.NotifyService;
import com.safetycampus.notify.service.PoliceStationService;
import com.safetycampus.school.entity.SchoolInfo;
import com.safetycampus.school.service.SchoolInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AlarmNotifyListener {

    @Resource
    private NotifyService notifyService;

    @Resource
    private PoliceStationService policeStationService;

    @Resource
    private ContactBookService contactBookService;

    @Resource
    private SchoolInfoService schoolInfoService;

    @RabbitListener(queues = "alarm.notify.queue")
    public void handleAlarmNotify(AlarmRecord alarmRecord) {
        log.info("收到警情通知消息 - 警情ID: {}, 标题: {}, 级别: {}",
                alarmRecord.getId(), alarmRecord.getAlarmTitle(), alarmRecord.getAlarmLevel());

        try {
            String title = "【警情通知】" + alarmRecord.getAlarmTitle();
            String content = String.format("警情编号：%s，学校：%s，位置：%s，详情：%s",
                    alarmRecord.getAlarmNo(),
                    getSchoolName(alarmRecord.getSchoolId()),
                    alarmRecord.getLocation(),
                    alarmRecord.getAlarmContent());

            boolean ruleResult = notifyService.notifyByRule(
                    alarmRecord.getSchoolId(),
                    alarmRecord.getAlarmLevel(),
                    alarmRecord.getAlarmType(),
                    alarmRecord.getId(),
                    title,
                    content
            );

            if (!ruleResult) {
                log.info("规则匹配失败或未配置规则，使用默认通知逻辑");
                if (AlarmLevelEnum.CRITICAL.getCode().equals(alarmRecord.getAlarmLevel())) {
                    handleCriticalAlarm(alarmRecord, title, content);
                } else if (AlarmLevelEnum.MAJOR.getCode().equals(alarmRecord.getAlarmLevel())) {
                    handleMajorAlarm(alarmRecord, title, content);
                } else {
                    handleGeneralAlarm(alarmRecord, title, content);
                }
            }

            log.info("警情通知处理完成 - 警情ID: {}", alarmRecord.getId());
        } catch (Exception e) {
            log.error("警情通知处理异常 - 警情ID: {}", alarmRecord.getId(), e);
        }
    }

    private void handleCriticalAlarm(AlarmRecord alarmRecord, String title, String content) {
        log.info("重大警情处理 - 启动多渠道联动通知");

        notifyPoliceStation(alarmRecord);

        List<ContactBook> dutyContacts = contactBookService.listDutyContacts();
        for (ContactBook contact : dutyContacts) {
            notifyService.sendSms(contact.getPhone(), contact.getName(), title, content, "SMS_ALARM", alarmRecord.getId());
            notifyService.sendAppPush(contact.getPhone(), contact.getName(), title, content, alarmRecord.getId());
        }

        List<ContactBook> educationContacts = contactBookService.listByUnitType(UnitTypeEnum.EDUCATION_BUREAU.getCode());
        for (ContactBook contact : educationContacts) {
            notifyService.sendSms(contact.getPhone(), contact.getName(), title, content, "SMS_ALARM", alarmRecord.getId());
        }
    }

    private void handleMajorAlarm(AlarmRecord alarmRecord, String title, String content) {
        log.info("较大警情处理 - 启动短信和APP推送通知");

        notifyPoliceStation(alarmRecord);

        List<ContactBook> dutyContacts = contactBookService.listDutyContacts();
        for (ContactBook contact : dutyContacts) {
            notifyService.sendSms(contact.getPhone(), contact.getName(), title, content, "SMS_ALARM", alarmRecord.getId());
        }
    }

    private void handleGeneralAlarm(AlarmRecord alarmRecord, String title, String content) {
        log.info("一般警情处理 - 启动APP推送通知");

        List<ContactBook> dutyContacts = contactBookService.listDutyContacts();
        for (ContactBook contact : dutyContacts) {
            notifyService.sendAppPush(contact.getPhone(), contact.getName(), title, content, alarmRecord.getId());
        }
    }

    private void notifyPoliceStation(AlarmRecord alarmRecord) {
        SchoolInfo school = schoolInfoService.getById(alarmRecord.getSchoolId());
        if (school != null && school.getPoliceStationId() != null) {
            PoliceStation station = policeStationService.getById(school.getPoliceStationId());
            if (station != null) {
                log.info("通知属地派出所 - 派出所: {}, 警情ID: {}", station.getStationName(), alarmRecord.getId());
                notifyService.notifyPoliceStation(
                        station.getId(),
                        alarmRecord.getId(),
                        alarmRecord.getAlarmTitle(),
                        alarmRecord.getAlarmContent()
                );
            }
        }
    }

    private String getSchoolName(Long schoolId) {
        if (schoolId == null) {
            return "未知";
        }
        SchoolInfo school = schoolInfoService.getById(schoolId);
        return school != null ? school.getSchoolName() : "未知";
    }
}

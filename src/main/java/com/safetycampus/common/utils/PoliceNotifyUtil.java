package com.safetycampus.common.utils;

import com.safetycampus.common.enums.AlarmDict;
import com.safetycampus.notify.entity.ContactBook;
import com.safetycampus.notify.entity.PoliceStation;
import com.safetycampus.notify.mapper.ContactBookMapper;
import com.safetycampus.notify.mapper.PoliceStationMapper;
import com.safetycampus.notify.service.NotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PoliceNotifyUtil {

    private final PoliceStationMapper policeStationMapper;
    private final ContactBookMapper contactBookMapper;
    private final NotifyService notifyService;

    public PoliceNotifyUtil(PoliceStationMapper policeStationMapper,
                            ContactBookMapper contactBookMapper,
                            NotifyService notifyService) {
        this.policeStationMapper = policeStationMapper;
        this.contactBookMapper = contactBookMapper;
        this.notifyService = notifyService;
    }

    public void notifyPoliceStation(Long policeStationId, String title, String content) {
        if (policeStationId == null) {
            log.warn("派出所ID为空，跳过通知");
            return;
        }

        PoliceStation station = policeStationMapper.selectById(policeStationId);
        if (station == null) {
            log.warn("派出所不存在，ID: {}", policeStationId);
            return;
        }

        List<ContactBook> contacts = contactBookMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ContactBook>()
                        .eq(ContactBook::getUnitType, AlarmDict.UNIT_TYPE_POLICE)
                        .eq(ContactBook::getUnitName, station.getStationName())
        );

        for (ContactBook contact : contacts) {
            try {
                notifyService.sendSms(contact.getPhone(), contact.getName(), title, content, null, null);
                log.info("已通知派出所联络人: {}, 电话: {}", contact.getName(), contact.getPhone());
            } catch (Exception e) {
                log.error("通知派出所联络人失败: {}, 电话: {}", contact.getName(), contact.getPhone(), e);
            }
        }

        if (station.getDutyPhone() != null) {
            try {
                notifyService.sendSms(station.getDutyPhone(), station.getStationName() + "值班", title, content, null, null);
                log.info("已通知派出所值班电话: {}", station.getDutyPhone());
            } catch (Exception e) {
                log.error("通知派出所值班电话失败: {}", station.getDutyPhone(), e);
            }
        }
    }

    public void notifyBySchool(Long schoolId, String title, String content) {
        List<ContactBook> contacts = contactBookMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ContactBook>()
                        .eq(ContactBook::getUnitType, AlarmDict.UNIT_TYPE_POLICE)
        );

        for (ContactBook contact : contacts) {
            try {
                notifyService.sendSms(contact.getPhone(), contact.getName(), title, content, null, null);
                log.info("已通知派出所联络人: {}, 电话: {}", contact.getName(), contact.getPhone());
            } catch (Exception e) {
                log.error("通知派出所联络人失败: {}, 电话: {}", contact.getName(), contact.getPhone(), e);
            }
        }
    }
}

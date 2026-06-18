package com.safetycampus.notify.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetycampus.common.entity.PageQuery;
import com.safetycampus.common.exception.BusinessException;
import com.safetycampus.notify.channel.AppPushChannel;
import com.safetycampus.notify.channel.SmsChannel;
import com.safetycampus.notify.channel.VoiceCallChannel;
import com.safetycampus.notify.dto.NotifySendDTO;
import com.safetycampus.notify.entity.NotifyRecord;
import com.safetycampus.notify.entity.PoliceStation;
import com.safetycampus.notify.enums.NotifyTypeEnum;
import com.safetycampus.notify.mapper.NotifyRecordMapper;
import com.safetycampus.notify.service.NotifyService;
import com.safetycampus.notify.service.PoliceStationService;
import com.safetycampus.notifyrule.entity.NotifyRule;
import com.safetycampus.notifyrule.entity.NotifyRuleTarget;
import com.safetycampus.notifyrule.service.NotifyRuleEngine;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NotifyServiceImpl extends ServiceImpl<NotifyRecordMapper, NotifyRecord> implements NotifyService {

    @Resource
    private NotifyRecordMapper notifyRecordMapper;

    @Resource
    private SmsChannel smsChannel;

    @Resource
    private AppPushChannel appPushChannel;

    @Resource
    private VoiceCallChannel voiceCallChannel;

    @Resource
    private PoliceStationService policeStationService;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private NotifyRuleEngine notifyRuleEngine;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendSms(String phone, String name, String title, String content, String templateCode, Long alarmId) {
        NotifyRecord record = createNotifyRecord(alarmId, NotifyTypeEnum.SMS.getCode(), phone, name, title, content, templateCode);
        try {
            Map<String, String> param = new HashMap<>();
            param.put("title", title);
            param.put("content", content);
            String templateParam = objectMapper.writeValueAsString(param);
            boolean success = smsChannel.send(phone, "平安校园", templateCode != null ? templateCode : "SMS_001", templateParam);
            if (success) {
                record.setStatus(1);
                record.setSentAt(LocalDateTime.now());
            } else {
                record.setStatus(2);
                record.setFailReason("短信发送失败");
            }
            updateById(record);
            return success;
        } catch (JsonProcessingException e) {
            log.error("短信参数序列化失败", e);
            record.setStatus(2);
            record.setFailReason("参数序列化失败: " + e.getMessage());
            updateById(record);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendAppPush(String target, String name, String title, String content, Long alarmId) {
        NotifyRecord record = createNotifyRecord(alarmId, NotifyTypeEnum.APP_PUSH.getCode(), target, name, title, content, null);
        try {
            boolean success = appPushChannel.push(target, title, content);
            if (success) {
                record.setStatus(1);
                record.setSentAt(LocalDateTime.now());
            } else {
                record.setStatus(2);
                record.setFailReason("APP推送失败");
            }
            updateById(record);
            return success;
        } catch (Exception e) {
            log.error("APP推送异常", e);
            record.setStatus(2);
            record.setFailReason("推送异常: " + e.getMessage());
            updateById(record);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendCall(String phone, String name, String title, String content, Long alarmId) {
        NotifyRecord record = createNotifyRecord(alarmId, NotifyTypeEnum.CALL.getCode(), phone, name, title, content, null);
        try {
            boolean success = voiceCallChannel.call(phone, name, content);
            if (success) {
                record.setStatus(1);
                record.setSentAt(LocalDateTime.now());
            } else {
                record.setStatus(2);
                record.setFailReason("电话呼叫失败");
            }
            updateById(record);
            return success;
        } catch (Exception e) {
            log.error("电话呼叫异常", e);
            record.setStatus(2);
            record.setFailReason("呼叫异常: " + e.getMessage());
            updateById(record);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchNotify(NotifySendDTO dto) {
        List<String> targets = dto.getTargets();
        List<String> targetNames = dto.getTargetNames();
        NotifyTypeEnum typeEnum = NotifyTypeEnum.values()[dto.getNotifyType() - 1];

        int successCount = 0;
        for (int i = 0; i < targets.size(); i++) {
            String target = targets.get(i);
            String name = (targetNames != null && i < targetNames.size()) ? targetNames.get(i) : null;
            boolean result = switch (typeEnum) {
                case SMS -> sendSms(target, name, dto.getTitle(), dto.getContent(), dto.getTemplateCode(), dto.getAlarmId());
                case APP_PUSH -> sendAppPush(target, name, dto.getTitle(), dto.getContent(), dto.getAlarmId());
                case CALL -> sendCall(target, name, dto.getTitle(), dto.getContent(), dto.getAlarmId());
                default -> false;
            };
            if (result) {
                successCount++;
            }
        }
        log.info("批量通知完成 - 类型: {}, 总数: {}, 成功: {}", typeEnum.getDesc(), targets.size(), successCount);
        return successCount > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean notifyPoliceStation(Long policeStationId, Long alarmId, String alarmTitle, String alarmContent) {
        PoliceStation station = policeStationService.getById(policeStationId);
        if (station == null) {
            throw new BusinessException("派出所不存在");
        }

        String title = "【警情通知】" + alarmTitle;
        String content = String.format("警情详情：%s，请及时处置。派出所：%s，联络人：%s，电话：%s",
                alarmContent, station.getStationName(), station.getLiaison(), station.getLiaisonPhone());

        boolean smsResult = true;
        boolean callResult = true;

        if (station.getLiaisonPhone() != null && !station.getLiaisonPhone().isEmpty()) {
            smsResult = sendSms(station.getLiaisonPhone(), station.getLiaison(), title, content, "SMS_ALARM", alarmId);
        }

        if (station.getDutyPhone() != null && !station.getDutyPhone().isEmpty()) {
            callResult = sendCall(station.getDutyPhone(), station.getStationName() + "值班", title, content, alarmId);
        }

        return smsResult || callResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NotifyRecord createNotifyRecord(Long alarmId, Integer notifyType, String notifyTarget, String targetName,
                                           String title, String content, String templateCode) {
        NotifyRecord record = new NotifyRecord();
        record.setAlarmId(alarmId);
        record.setNotifyType(notifyType);
        record.setNotifyTarget(notifyTarget);
        record.setTargetName(targetName);
        record.setTitle(title);
        record.setContent(content);
        record.setTemplateCode(templateCode);
        record.setStatus(0);
        save(record);
        return record;
    }

    @Override
    public IPage<NotifyRecord> selectPage(PageQuery query, Long alarmId, Integer notifyType, Integer status) {
        LambdaQueryWrapper<NotifyRecord> wrapper = new LambdaQueryWrapper<>();
        if (alarmId != null) {
            wrapper.eq(NotifyRecord::getAlarmId, alarmId);
        }
        if (notifyType != null) {
            wrapper.eq(NotifyRecord::getNotifyType, notifyType);
        }
        if (status != null) {
            wrapper.eq(NotifyRecord::getStatus, status);
        }
        wrapper.orderByDesc(NotifyRecord::getCreatedAt);
        return page(query.buildPage(), wrapper);
    }

    @Override
    public List<NotifyRecord> listByAlarmId(Long alarmId) {
        LambdaQueryWrapper<NotifyRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NotifyRecord::getAlarmId, alarmId);
        wrapper.orderByDesc(NotifyRecord::getCreatedAt);
        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsRead(Long id) {
        NotifyRecord record = getById(id);
        if (record == null) {
            throw new BusinessException("通知记录不存在");
        }
        record.setReadAt(LocalDateTime.now());
        return updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resend(Long id) {
        NotifyRecord record = getById(id);
        if (record == null) {
            throw new BusinessException("通知记录不存在");
        }

        NotifyTypeEnum typeEnum = NotifyTypeEnum.values()[record.getNotifyType() - 1];
        return switch (typeEnum) {
            case SMS -> sendSms(record.getNotifyTarget(), record.getTargetName(), record.getTitle(),
                    record.getContent(), record.getTemplateCode(), record.getAlarmId());
            case APP_PUSH -> sendAppPush(record.getNotifyTarget(), record.getTargetName(), record.getTitle(),
                    record.getContent(), record.getAlarmId());
            case CALL -> sendCall(record.getNotifyTarget(), record.getTargetName(), record.getTitle(),
                    record.getContent(), record.getAlarmId());
            default -> false;
        };
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean notifyByRule(Long schoolId, Integer alarmLevel, Integer alarmType, Long alarmId, String title, String content) {
        log.info("根据规则发送通知 - schoolId: {}, alarmLevel: {}, alarmType: {}", schoolId, alarmLevel, alarmType);

        List<NotifyRule> matchedRules = notifyRuleEngine.matchRules(schoolId, alarmLevel, alarmType);
        if (matchedRules.isEmpty()) {
            log.warn("未匹配到任何通知规则，使用默认逻辑");
            return false;
        }

        List<NotifyRuleTarget> targets = notifyRuleEngine.getTargetsByRules(matchedRules);
        List<Integer> channels = notifyRuleEngine.getNotifyChannelsByRules(matchedRules);
        String template = notifyRuleEngine.getNotifyTemplateByRules(matchedRules);

        if (targets.isEmpty()) {
            log.warn("匹配到规则但没有通知目标");
            return false;
        }

        String notifyTitle = title;
        String notifyContent = content;
        if (template != null && !template.isEmpty()) {
            notifyContent = template.replace("{title}", title).replace("{content}", content);
        }

        int successCount = 0;
        for (NotifyRuleTarget target : targets) {
            String targetPhone = target.getTargetPhone();
            String targetName = target.getTargetName();
            if (targetPhone == null || targetPhone.isEmpty()) {
                continue;
            }
            for (Integer channel : channels) {
                boolean result = switch (channel) {
                    case 1 -> sendSms(targetPhone, targetName, notifyTitle, notifyContent, "SMS_ALARM", alarmId);
                    case 2 -> sendAppPush(targetPhone, targetName, notifyTitle, notifyContent, alarmId);
                    case 3 -> sendCall(targetPhone, targetName, notifyTitle, notifyContent, alarmId);
                    case 4 -> true;
                    default -> false;
                };
                if (result) {
                    successCount++;
                }
            }
        }

        log.info("规则通知完成 - 匹配规则数: {}, 目标数: {}, 渠道数: {}, 成功次数: {}",
                matchedRules.size(), targets.size(), channels.size(), successCount);

        return successCount > 0;
    }
}

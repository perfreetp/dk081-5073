package com.safetycampus.alarm.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.safetycampus.alarm.entity.AlarmFlow;
import com.safetycampus.alarm.entity.AlarmRecord;
import com.safetycampus.alarm.entity.AlarmRemind;
import com.safetycampus.alarm.enums.AlarmStatusEnum;
import com.safetycampus.alarm.mapper.AlarmRecordMapper;
import com.safetycampus.alarm.mapper.AlarmRemindMapper;
import com.safetycampus.alarm.service.AlarmFlowService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class AlarmTimeoutTask {

    @Resource
    private AlarmRemindMapper alarmRemindMapper;

    @Resource
    private AlarmRecordMapper alarmRecordMapper;

    @Resource
    private AlarmFlowService alarmFlowService;

    @Scheduled(fixedRate = 60000)
    @Transactional(rollbackFor = Exception.class)
    public void processTimeoutRemind() {
        log.info("开始执行超时催办定时任务");

        LocalDateTime now = LocalDateTime.now();

        LambdaQueryWrapper<AlarmRemind> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmRemind::getStatus, 1);
        wrapper.le(AlarmRemind::getNextRemindAt, now);
        List<AlarmRemind> remindList = alarmRemindMapper.selectList(wrapper);

        for (AlarmRemind remind : remindList) {
            processRemind(remind, now);
        }

        log.info("超时催办定时任务执行完成，处理催办记录：{}条", remindList.size());
    }

    private void processRemind(AlarmRemind remind, LocalDateTime now) {
        AlarmRecord record = alarmRecordMapper.selectById(remind.getAlarmId());
        if (record == null) {
            remind.setStatus(2);
            alarmRemindMapper.updateById(remind);
            return;
        }

        if (AlarmStatusEnum.HANDLED.getCode().equals(record.getStatus())
                || AlarmStatusEnum.CLOSED.getCode().equals(record.getStatus())) {
            remind.setStatus(2);
            alarmRemindMapper.updateById(remind);
            return;
        }

        if (remind.getRemindCount() >= 3) {
            escalateToSupervisor(record, remind);
            remind.setStatus(2);
        } else {
            sendRemindNotification(record, remind);
            remind.setRemindCount(remind.getRemindCount() + 1);
            remind.setLastRemindAt(now);
            remind.setNextRemindAt(now.plusMinutes(10));
        }

        alarmRemindMapper.updateById(remind);
    }

    private void sendRemindNotification(AlarmRecord record, AlarmRemind remind) {
        String remindType = remind.getRemindType() == 1 ? "首次响应超时" : "处置超时";
        log.warn("警情[{}]{}催办，第{}次，警情编号：{}",
                record.getId(), remindType, remind.getRemindCount() + 1, record.getAlarmNo());

        AlarmFlow flow = new AlarmFlow();
        flow.setAlarmId(record.getId());
        flow.setFlowType(6);
        flow.setOperatorName("系统");
        flow.setRemark(remindType + "催办，第" + (remind.getRemindCount() + 1) + "次");
        alarmFlowService.save(flow);
    }

    private void escalateToSupervisor(AlarmRecord record, AlarmRemind remind) {
        log.warn("警情[{}]多次催办未响应，升级督办，警情编号：{}", record.getId(), record.getAlarmNo());

        record.setStatus(AlarmStatusEnum.SUPERVISING.getCode());
        alarmRecordMapper.updateById(record);

        String remindType = remind.getRemindType() == 1 ? "首次响应超时" : "处置超时";
        AlarmFlow flow = new AlarmFlow();
        flow.setAlarmId(record.getId());
        flow.setFlowType(3);
        flow.setOperatorName("系统");
        flow.setRemark(remindType + "多次催办未响应，自动升级督办");
        alarmFlowService.save(flow);
    }
}

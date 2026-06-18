package com.safetycampus.supervise.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.safetycampus.alarm.entity.AlarmRecord;
import com.safetycampus.alarm.entity.AlarmRemind;
import com.safetycampus.alarm.enums.AlarmStatusEnum;
import com.safetycampus.alarm.mapper.AlarmRecordMapper;
import com.safetycampus.alarm.service.AlarmFlowService;
import com.safetycampus.common.context.LoginUser;
import com.safetycampus.common.context.UserContext;
import com.safetycampus.common.enums.RoleTypeEnum;
import com.safetycampus.common.exception.BusinessException;
import com.safetycampus.common.result.ResultCode;
import com.safetycampus.supervise.dto.AlarmTransferDTO;
import com.safetycampus.supervise.dto.HandleFeedbackDTO;
import com.safetycampus.supervise.dto.SuperviseCreateDTO;
import com.safetycampus.supervise.enums.FlowTypeEnum;
import com.safetycampus.supervise.service.SuperviseService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SuperviseServiceImpl extends ServiceImpl<AlarmRecordMapper, AlarmRecord> implements SuperviseService {

    @Resource
    private AlarmFlowService alarmFlowService;

    @Resource
    private com.safetycampus.alarm.mapper.AlarmRemindMapper alarmRemindMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createSupervise(SuperviseCreateDTO dto) {
        AlarmRecord record = getById(dto.getAlarmId());
        if (record == null) {
            throw new BusinessException(ResultCode.ALARM_NOT_EXIST);
        }
        if (AlarmStatusEnum.CLOSED.getCode().equals(record.getStatus())) {
            throw new BusinessException(ResultCode.ALARM_STATUS_ERROR, "已关闭的警情不能督办");
        }

        LoginUser loginUser = UserContext.getLoginUser();
        Long supervisorId = dto.getSupervisorId();
        String supervisorName = dto.getSupervisorName();
        if (supervisorId == null && loginUser != null) {
            supervisorId = loginUser.getUserId();
            supervisorName = loginUser.getRealName();
        }

        record.setStatus(AlarmStatusEnum.SUPERVISING.getCode());
        record.setSupervisorId(supervisorId);
        updateById(record);

        String remark = "督办要求：" + dto.getSuperviseRequire();
        alarmFlowService.addFlowRecord(dto.getAlarmId(), FlowTypeEnum.SUPERVISE_CREATE.getCode(),
                supervisorId, supervisorName, RoleTypeEnum.EDUCATION_BUREAU.getDesc(), remark);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleFeedback(HandleFeedbackDTO dto) {
        AlarmRecord record = getById(dto.getAlarmId());
        if (record == null) {
            throw new BusinessException(ResultCode.ALARM_NOT_EXIST);
        }
        if (AlarmStatusEnum.CLOSED.getCode().equals(record.getStatus())) {
            throw new BusinessException(ResultCode.ALARM_STATUS_ERROR, "已关闭的警情不能反馈");
        }
        if (!AlarmStatusEnum.HANDLING.getCode().equals(dto.getHandleStatus())
                && !AlarmStatusEnum.HANDLED.getCode().equals(dto.getHandleStatus())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "处置状态不正确");
        }

        LoginUser loginUser = UserContext.getLoginUser();
        Long handlerId = dto.getHandlerId();
        String handlerName = dto.getHandlerName();
        String operatorRole = RoleTypeEnum.SCHOOL_SECURITY.getDesc();
        if (handlerId == null && loginUser != null) {
            handlerId = loginUser.getUserId();
            handlerName = loginUser.getRealName();
            if (loginUser.getRoleType() != null) {
                operatorRole = loginUser.getRoleType().getDesc();
            }
        }

        LocalDateTime now = LocalDateTime.now();
        if (record.getFirstResponseAt() == null) {
            record.setFirstResponseAt(now);
        }
        record.setStatus(dto.getHandleStatus());
        record.setHandlerId(handlerId);
        if (AlarmStatusEnum.HANDLED.getCode().equals(dto.getHandleStatus())) {
            record.setHandledAt(now);
        }
        updateById(record);

        String statusDesc = AlarmStatusEnum.getDescByCode(dto.getHandleStatus());
        String remark = "处置状态：" + statusDesc + "，处置结果：" + dto.getHandleResult();
        alarmFlowService.addFlowRecord(dto.getAlarmId(), FlowTypeEnum.HANDLE_FEEDBACK.getCode(),
                handlerId, handlerName, operatorRole, remark, dto.getAttachUrl());

        stopAlarmRemind(dto.getAlarmId());

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean transferAlarm(AlarmTransferDTO dto) {
        AlarmRecord record = getById(dto.getAlarmId());
        if (record == null) {
            throw new BusinessException(ResultCode.ALARM_NOT_EXIST);
        }
        if (AlarmStatusEnum.CLOSED.getCode().equals(record.getStatus())) {
            throw new BusinessException(ResultCode.ALARM_STATUS_ERROR, "已关闭的警情不能转派");
        }

        LoginUser loginUser = UserContext.getLoginUser();
        Long operatorId = dto.getOperatorId();
        String operatorName = dto.getOperatorName();
        if (operatorId == null && loginUser != null) {
            operatorId = loginUser.getUserId();
            operatorName = loginUser.getRealName();
        }

        record.setSchoolId(dto.getTargetSchoolId());
        record.setStatus(AlarmStatusEnum.PENDING.getCode());
        record.setHandlerId(null);
        updateById(record);

        String remark = "转派至：" + dto.getTargetSchoolName() + "，转派原因：" + dto.getTransferReason();
        alarmFlowService.addFlowRecord(dto.getAlarmId(), FlowTypeEnum.ALARM_TRANSFER.getCode(),
                operatorId, operatorName, RoleTypeEnum.EDUCATION_BUREAU.getDesc(), remark);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closeAlarm(Long id, String remark) {
        AlarmRecord record = getById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.ALARM_NOT_EXIST);
        }
        if (AlarmStatusEnum.CLOSED.getCode().equals(record.getStatus())) {
            throw new BusinessException(ResultCode.ALARM_STATUS_ERROR, "该警情已关闭");
        }

        LoginUser loginUser = UserContext.getLoginUser();
        Long operatorId = null;
        String operatorName = null;
        String operatorRole = null;
        if (loginUser != null) {
            operatorId = loginUser.getUserId();
            operatorName = loginUser.getRealName();
            if (loginUser.getRoleType() != null) {
                operatorRole = loginUser.getRoleType().getDesc();
            }
        }

        record.setStatus(AlarmStatusEnum.CLOSED.getCode());
        record.setClosedAt(LocalDateTime.now());
        updateById(record);

        String flowRemark = remark != null ? remark : "警情已关闭";
        alarmFlowService.addFlowRecord(id, FlowTypeEnum.ALARM_CLOSE.getCode(),
                operatorId, operatorName, operatorRole, flowRemark);

        stopAlarmRemind(id);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean manualRemind(Long id) {
        AlarmRecord record = getById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.ALARM_NOT_EXIST);
        }
        if (AlarmStatusEnum.CLOSED.getCode().equals(record.getStatus())) {
            throw new BusinessException(ResultCode.ALARM_STATUS_ERROR, "已关闭的警情不能催办");
        }
        if (AlarmStatusEnum.HANDLED.getCode().equals(record.getStatus())) {
            throw new BusinessException(ResultCode.ALARM_STATUS_ERROR, "已处置的警情不能催办");
        }

        LoginUser loginUser = UserContext.getLoginUser();
        Long operatorId = null;
        String operatorName = null;
        if (loginUser != null) {
            operatorId = loginUser.getUserId();
            operatorName = loginUser.getRealName();
        }

        alarmFlowService.addFlowRecord(id, FlowTypeEnum.REMIND_MANUAL.getCode(),
                operatorId, operatorName, RoleTypeEnum.EDUCATION_BUREAU.getDesc(), "手动催办，请尽快处置");

        updateAlarmRemind(id);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remindTimeout() {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<AlarmRemind> remindWrapper = new LambdaQueryWrapper<>();
        remindWrapper.eq(AlarmRemind::getStatus, 1);
        remindWrapper.le(AlarmRemind::getNextRemindAt, now);
        List<AlarmRemind> remindList = alarmRemindMapper.selectList(remindWrapper);

        for (AlarmRemind remind : remindList) {
            AlarmRecord record = getById(remind.getAlarmId());
            if (record == null || AlarmStatusEnum.CLOSED.getCode().equals(record.getStatus())
                    || AlarmStatusEnum.HANDLED.getCode().equals(record.getStatus())) {
                remind.setStatus(0);
                alarmRemindMapper.updateById(remind);
                continue;
            }

            alarmFlowService.addFlowRecord(remind.getAlarmId(), FlowTypeEnum.REMIND_TIMEOUT.getCode(),
                    null, "系统", "系统", "超时自动催办，请尽快处置");

            remind.setRemindCount(remind.getRemindCount() + 1);
            remind.setLastRemindAt(now);
            remind.setNextRemindAt(now.plusMinutes(30));
            alarmRemindMapper.updateById(remind);
        }
    }

    private void stopAlarmRemind(Long alarmId) {
        LambdaQueryWrapper<AlarmRemind> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmRemind::getAlarmId, alarmId);
        wrapper.eq(AlarmRemind::getStatus, 1);
        List<AlarmRemind> remindList = alarmRemindMapper.selectList(wrapper);
        for (AlarmRemind remind : remindList) {
            remind.setStatus(0);
            alarmRemindMapper.updateById(remind);
        }
    }

    private void updateAlarmRemind(Long alarmId) {
        LambdaQueryWrapper<AlarmRemind> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmRemind::getAlarmId, alarmId);
        wrapper.eq(AlarmRemind::getStatus, 1);
        AlarmRemind remind = alarmRemindMapper.selectOne(wrapper);
        if (remind != null) {
            LocalDateTime now = LocalDateTime.now();
            remind.setRemindCount(remind.getRemindCount() + 1);
            remind.setLastRemindAt(now);
            remind.setNextRemindAt(now.plusMinutes(30));
            alarmRemindMapper.updateById(remind);
        }
    }
}

package com.safetycampus.plan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.safetycampus.alarm.entity.AlarmFlow;
import com.safetycampus.alarm.entity.AlarmRecord;
import com.safetycampus.alarm.mapper.AlarmRecordMapper;
import com.safetycampus.alarm.service.AlarmFlowService;
import com.safetycampus.common.exception.BusinessException;
import com.safetycampus.common.result.ResultCode;
import com.safetycampus.plan.dto.PlanStepExecDTO;
import com.safetycampus.plan.entity.AlarmPlanLink;
import com.safetycampus.plan.entity.AlarmPlanStepExec;
import com.safetycampus.plan.entity.DisposalPlan;
import com.safetycampus.plan.entity.DisposalPlanStep;
import com.safetycampus.plan.enums.PlanMatchStatusEnum;
import com.safetycampus.plan.enums.StepExecStatusEnum;
import com.safetycampus.plan.mapper.AlarmPlanLinkMapper;
import com.safetycampus.plan.mapper.AlarmPlanStepExecMapper;
import com.safetycampus.plan.mapper.DisposalPlanMapper;
import com.safetycampus.plan.mapper.DisposalPlanStepMapper;
import com.safetycampus.plan.service.AlarmPlanService;
import com.safetycampus.plan.vo.AlarmPlanDetailVO;
import com.safetycampus.plan.vo.PlanMatchResultVO;
import com.safetycampus.school.entity.SchoolInfo;
import com.safetycampus.school.mapper.SchoolInfoMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class AlarmPlanServiceImpl extends ServiceImpl<AlarmPlanLinkMapper, AlarmPlanLink> implements AlarmPlanService {

    @Resource
    private AlarmRecordMapper alarmRecordMapper;

    @Resource
    private SchoolInfoMapper schoolInfoMapper;

    @Resource
    private DisposalPlanMapper disposalPlanMapper;

    @Resource
    private DisposalPlanStepMapper disposalPlanStepMapper;

    @Resource
    private AlarmPlanStepExecMapper alarmPlanStepExecMapper;

    @Resource
    private AlarmFlowService alarmFlowService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PlanMatchResultVO matchAndLinkPlan(Long alarmId) {
        if (alarmId == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "alarmId");
        }

        AlarmRecord alarm = alarmRecordMapper.selectById(alarmId);
        if (alarm == null) {
            throw BusinessException.of(ResultCode.ALARM_NOT_FOUND);
        }

        SchoolInfo school = schoolInfoMapper.selectById(alarm.getSchoolId());
        if (school == null) {
            throw BusinessException.of(ResultCode.SCHOOL_NOT_FOUND);
        }

        Integer schoolType = school.getSchoolType();
        Integer alarmLevel = alarm.getAlarmLevel();

        LambdaQueryWrapper<DisposalPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DisposalPlan::getAlarmLevel, alarmLevel);
        wrapper.eq(DisposalPlan::getIsEnabled, 1);
        wrapper.orderByAsc(DisposalPlan::getSortOrder);
        wrapper.orderByDesc(DisposalPlan::getId);

        List<DisposalPlan> allPlans = disposalPlanMapper.selectList(wrapper);

        DisposalPlan matchedPlan = null;
        for (DisposalPlan plan : allPlans) {
            if (!StringUtils.hasText(plan.getSchoolTypes())) {
                matchedPlan = plan;
                break;
            }
            String[] types = plan.getSchoolTypes().split(",");
            for (String type : types) {
                if (Integer.parseInt(type.trim()) == schoolType) {
                    matchedPlan = plan;
                    break;
                }
            }
            if (matchedPlan != null) {
                break;
            }
        }

        PlanMatchResultVO result = new PlanMatchResultVO();
        if (matchedPlan == null) {
            result.setMatchReason("未找到匹配的处置预案(警情级别:" + alarmLevel + ",学校类型:" + schoolType + ")");
            return result;
        }

        List<DisposalPlanStep> steps = disposalPlanStepMapper.selectByPlanId(matchedPlan.getId());

        AlarmPlanLink existingLink = baseMapper.selectLatestByAlarmId(alarmId);
        if (existingLink != null && 
            (PlanMatchStatusEnum.STARTED.getCode().equals(existingLink.getStatus()) ||
             PlanMatchStatusEnum.EXECUTING.getCode().equals(existingLink.getStatus()))) {
            result.setPlan(matchedPlan);
            result.setSteps(steps);
            result.setSuggestedTimeLimit(calculateTotalTimeLimit(steps));
            result.setMatchReason("已有进行中的预案，无需重复匹配");
            return result;
        }

        AlarmPlanLink link = new AlarmPlanLink();
        link.setAlarmId(alarmId);
        link.setPlanId(matchedPlan.getId());
        link.setMatchedTime(LocalDateTime.now());
        link.setStatus(PlanMatchStatusEnum.MATCHED.getCode());
        link.setCompletionRate(BigDecimal.ZERO);
        baseMapper.insert(link);

        int stepIndex = 1;
        for (DisposalPlanStep step : steps) {
            AlarmPlanStepExec exec = new AlarmPlanStepExec();
            exec.setLinkId(link.getId());
            exec.setAlarmId(alarmId);
            exec.setPlanStepId(step.getId());
            exec.setStepNo(step.getStepNo() != null ? step.getStepNo() : stepIndex++);
            exec.setStepName(step.getStepName());
            exec.setResponsibleUnit(step.getResponsibleUnit());
            exec.setTimeLimitMinutes(step.getTimeLimitMinutes() != null ? step.getTimeLimitMinutes() : 0);
            exec.setExecStatus(StepExecStatusEnum.NOT_STARTED.getCode());
            exec.setDurationSeconds(0);
            alarmPlanStepExecMapper.insert(exec);
        }

        AlarmFlow flow = new AlarmFlow();
        flow.setAlarmId(alarmId);
        flow.setFlowType(8);
        flow.setOperatorName("系统");
        flow.setPartyType(4);
        flow.setPartyName("系统");
        flow.setRemark("预案联动：匹配预案【" + matchedPlan.getPlanName() + "】，共" + steps.size() + "个步骤");
        alarmFlowService.save(flow);

        result.setPlan(matchedPlan);
        result.setSteps(steps);
        result.setSuggestedTimeLimit(calculateTotalTimeLimit(steps));
        result.setMatchReason("匹配成功：根据警情级别" + alarmLevel + "和学校类型" + schoolType + "匹配到【" + matchedPlan.getPlanName() + "】");

        return result;
    }

    @Override
    public AlarmPlanDetailVO getAlarmPlanDetail(Long alarmId) {
        if (alarmId == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "alarmId");
        }

        AlarmPlanLink link = baseMapper.selectLatestByAlarmId(alarmId);
        if (link == null) {
            return null;
        }

        DisposalPlan plan = disposalPlanMapper.selectById(link.getPlanId());
        List<AlarmPlanStepExec> stepExecList = alarmPlanStepExecMapper.selectByLinkId(link.getId());

        Long totalCount = alarmPlanStepExecMapper.countTotalByLinkId(link.getId());
        Long completedCount = alarmPlanStepExecMapper.countCompletedByLinkId(link.getId());

        int total = totalCount != null ? totalCount.intValue() : 0;
        int completed = completedCount != null ? completedCount.intValue() : 0;

        BigDecimal completionRate = BigDecimal.ZERO;
        if (total > 0) {
            completionRate = BigDecimal.valueOf(completed)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
        }

        AlarmPlanDetailVO vo = new AlarmPlanDetailVO();
        vo.setPlan(plan);
        vo.setLink(link);
        vo.setTotalSteps(total);
        vo.setCompletedSteps(completed);
        vo.setProgressText(completed + "/" + total);
        vo.setCompletionRate(completionRate);
        vo.setStepExecList(stepExecList);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startPlanExecution(Long linkId) {
        if (linkId == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "linkId");
        }

        AlarmPlanLink link = getById(linkId);
        if (link == null) {
            throw BusinessException.of(ResultCode.PARAM_VALIDATE_ERROR);
        }

        if (!PlanMatchStatusEnum.MATCHED.getCode().equals(link.getStatus())) {
            throw BusinessException.of(ResultCode.ALARM_STATUS_NOT_ALLOWED);
        }

        link.setStatus(PlanMatchStatusEnum.EXECUTING.getCode());
        link.setStartedAt(LocalDateTime.now());
        boolean updated = updateById(link);
        if (!updated) {
            throw BusinessException.of(ResultCode.DATABASE_ERROR);
        }

        List<AlarmPlanStepExec> steps = alarmPlanStepExecMapper.selectByLinkId(linkId);
        if (!steps.isEmpty()) {
            AlarmPlanStepExec firstStep = steps.get(0);
            if (StepExecStatusEnum.NOT_STARTED.getCode().equals(firstStep.getExecStatus())) {
                firstStep.setExecStatus(StepExecStatusEnum.IN_PROGRESS.getCode());
                alarmPlanStepExecMapper.updateById(firstStep);
            }
        }

        DisposalPlan plan = disposalPlanMapper.selectById(link.getPlanId());
        AlarmFlow flow = new AlarmFlow();
        flow.setAlarmId(link.getAlarmId());
        flow.setFlowType(8);
        flow.setOperatorName("系统");
        flow.setPartyType(4);
        flow.setPartyName("系统");
        flow.setRemark("预案启动：开始执行【" + (plan != null ? plan.getPlanName() : "") + "】");
        alarmFlowService.save(flow);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeStep(PlanStepExecDTO dto) {
        if (dto.getLinkId() == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "linkId");
        }
        if (dto.getStepId() == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "stepId");
        }

        AlarmPlanLink link = getById(dto.getLinkId());
        if (link == null) {
            throw BusinessException.of(ResultCode.PARAM_VALIDATE_ERROR);
        }

        if (!PlanMatchStatusEnum.EXECUTING.getCode().equals(link.getStatus()) &&
            !PlanMatchStatusEnum.STARTED.getCode().equals(link.getStatus())) {
            throw BusinessException.of(ResultCode.ALARM_STATUS_NOT_ALLOWED);
        }

        AlarmPlanStepExec stepExec = alarmPlanStepExecMapper.selectById(dto.getStepId());
        if (stepExec == null) {
            throw BusinessException.of(ResultCode.PARAM_VALIDATE_ERROR);
        }

        if (StepExecStatusEnum.COMPLETED.getCode().equals(stepExec.getExecStatus())) {
            throw BusinessException.of(ResultCode.ALARM_STATUS_NOT_ALLOWED);
        }

        Integer targetStatus = dto.getExecStatus() != null ? dto.getExecStatus() : StepExecStatusEnum.COMPLETED.getCode();
        if (!StepExecStatusEnum.IN_PROGRESS.getCode().equals(targetStatus) &&
            !StepExecStatusEnum.COMPLETED.getCode().equals(targetStatus)) {
            throw BusinessException.of(ResultCode.PARAM_VALIDATE_ERROR);
        }

        LocalDateTime now = LocalDateTime.now();
        stepExec.setExecStatus(targetStatus);
        stepExec.setExecRemark(dto.getExecRemark());
        stepExec.setAttachUrl(dto.getAttachUrl());
        stepExec.setExecutedBy(dto.getExecutedBy());

        if (StepExecStatusEnum.COMPLETED.getCode().equals(targetStatus)) {
            stepExec.setExecutedAt(now);
            if (link.getStartedAt() != null) {
                long seconds = Duration.between(link.getStartedAt(), now).getSeconds();
                stepExec.setDurationSeconds((int) seconds);
            }
        }

        boolean updated = alarmPlanStepExecMapper.updateById(stepExec) > 0;
        if (!updated) {
            throw BusinessException.of(ResultCode.DATABASE_ERROR);
        }

        updateLinkProgress(dto.getLinkId());

        if (StepExecStatusEnum.COMPLETED.getCode().equals(targetStatus)) {
            List<AlarmPlanStepExec> allSteps = alarmPlanStepExecMapper.selectByLinkId(dto.getLinkId());
            for (AlarmPlanStepExec s : allSteps) {
                if (StepExecStatusEnum.NOT_STARTED.getCode().equals(s.getExecStatus())) {
                    s.setExecStatus(StepExecStatusEnum.IN_PROGRESS.getCode());
                    alarmPlanStepExecMapper.updateById(s);
                    break;
                }
            }
        }

        AlarmFlow flow = new AlarmFlow();
        flow.setAlarmId(link.getAlarmId());
        flow.setFlowType(5);
        flow.setOperatorId(dto.getExecutedBy());
        flow.setRemark("预案步骤" + (StepExecStatusEnum.COMPLETED.getCode().equals(targetStatus) ? "完成" : "开始") +
                "：步骤" + stepExec.getStepNo() + "【" + stepExec.getStepName() + "】" +
                (dto.getExecRemark() != null ? "，备注：" + dto.getExecRemark() : ""));
        alarmFlowService.save(flow);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void skipStep(PlanStepExecDTO dto) {
        if (dto.getLinkId() == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "linkId");
        }
        if (dto.getStepId() == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "stepId");
        }

        AlarmPlanLink link = getById(dto.getLinkId());
        if (link == null) {
            throw BusinessException.of(ResultCode.PARAM_VALIDATE_ERROR);
        }

        if (!PlanMatchStatusEnum.EXECUTING.getCode().equals(link.getStatus()) &&
            !PlanMatchStatusEnum.STARTED.getCode().equals(link.getStatus())) {
            throw BusinessException.of(ResultCode.ALARM_STATUS_NOT_ALLOWED);
        }

        AlarmPlanStepExec stepExec = alarmPlanStepExecMapper.selectById(dto.getStepId());
        if (stepExec == null) {
            throw BusinessException.of(ResultCode.PARAM_VALIDATE_ERROR);
        }

        if (StepExecStatusEnum.COMPLETED.getCode().equals(stepExec.getExecStatus())) {
            throw BusinessException.of(ResultCode.ALARM_STATUS_NOT_ALLOWED);
        }

        stepExec.setExecStatus(StepExecStatusEnum.SKIPPED.getCode());
        stepExec.setExecRemark(dto.getExecRemark());
        stepExec.setAttachUrl(dto.getAttachUrl());
        stepExec.setExecutedBy(dto.getExecutedBy());
        stepExec.setExecutedAt(LocalDateTime.now());

        boolean updated = alarmPlanStepExecMapper.updateById(stepExec) > 0;
        if (!updated) {
            throw BusinessException.of(ResultCode.DATABASE_ERROR);
        }

        updateLinkProgress(dto.getLinkId());

        List<AlarmPlanStepExec> allSteps = alarmPlanStepExecMapper.selectByLinkId(dto.getLinkId());
        for (AlarmPlanStepExec s : allSteps) {
            if (StepExecStatusEnum.NOT_STARTED.getCode().equals(s.getExecStatus())) {
                s.setExecStatus(StepExecStatusEnum.IN_PROGRESS.getCode());
                alarmPlanStepExecMapper.updateById(s);
                break;
            }
        }

        AlarmFlow flow = new AlarmFlow();
        flow.setAlarmId(link.getAlarmId());
        flow.setFlowType(5);
        flow.setOperatorId(dto.getExecutedBy());
        flow.setRemark("预案步骤跳过：步骤" + stepExec.getStepNo() + "【" + stepExec.getStepName() + "】" +
                (dto.getExecRemark() != null ? "，原因：" + dto.getExecRemark() : ""));
        alarmFlowService.save(flow);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completePlan(Long linkId, String summary) {
        if (linkId == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "linkId");
        }

        AlarmPlanLink link = getById(linkId);
        if (link == null) {
            throw BusinessException.of(ResultCode.PARAM_VALIDATE_ERROR);
        }

        if (!PlanMatchStatusEnum.EXECUTING.getCode().equals(link.getStatus()) &&
            !PlanMatchStatusEnum.STARTED.getCode().equals(link.getStatus())) {
            throw BusinessException.of(ResultCode.ALARM_STATUS_NOT_ALLOWED);
        }

        Long total = alarmPlanStepExecMapper.countTotalByLinkId(linkId);
        Long completed = alarmPlanStepExecMapper.countCompletedByLinkId(linkId);
        int totalInt = total != null ? total.intValue() : 0;
        int completedInt = completed != null ? completed.intValue() : 0;

        BigDecimal rate = BigDecimal.ZERO;
        if (totalInt > 0) {
            rate = BigDecimal.valueOf(completedInt)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalInt), 2, RoundingMode.HALF_UP);
        }

        link.setStatus(PlanMatchStatusEnum.COMPLETED.getCode());
        link.setCompletedAt(LocalDateTime.now());
        link.setCompletionRate(rate);
        link.setSummary(summary);

        boolean updated = updateById(link);
        if (!updated) {
            throw BusinessException.of(ResultCode.DATABASE_ERROR);
        }

        DisposalPlan plan = disposalPlanMapper.selectById(link.getPlanId());
        AlarmFlow flow = new AlarmFlow();
        flow.setAlarmId(link.getAlarmId());
        flow.setFlowType(8);
        flow.setOperatorName("系统");
        flow.setPartyType(4);
        flow.setPartyName("系统");
        flow.setRemark("预案完成：【" + (plan != null ? plan.getPlanName() : "") + "】，完成率" + rate + "%" +
                (summary != null ? "，总结：" + summary : ""));
        alarmFlowService.save(flow);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeAlarmWithPlan(Long alarmId, Long linkId, String summary) {
        if (alarmId == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "alarmId");
        }

        AlarmRecord alarm = alarmRecordMapper.selectById(alarmId);
        if (alarm == null) {
            throw BusinessException.of(ResultCode.ALARM_NOT_FOUND);
        }

        if (linkId == null) {
            AlarmPlanLink latestLink = baseMapper.selectLatestByAlarmId(alarmId);
            if (latestLink != null) {
                linkId = latestLink.getId();
            }
        }

        if (linkId != null) {
            AlarmPlanLink link = getById(linkId);
            if (link != null && !PlanMatchStatusEnum.COMPLETED.getCode().equals(link.getStatus())) {
                List<AlarmPlanStepExec> steps = alarmPlanStepExecMapper.selectByLinkId(linkId);
                LocalDateTime now = LocalDateTime.now();
                for (AlarmPlanStepExec step : steps) {
                    if (StepExecStatusEnum.NOT_STARTED.getCode().equals(step.getExecStatus()) ||
                        StepExecStatusEnum.IN_PROGRESS.getCode().equals(step.getExecStatus())) {
                        step.setExecStatus(StepExecStatusEnum.SKIPPED.getCode());
                        step.setExecRemark("警情关闭时自动跳过");
                        step.setExecutedAt(now);
                        alarmPlanStepExecMapper.updateById(step);
                    }
                }

                completePlan(linkId, summary);
            }
        }
    }

    private int calculateTotalTimeLimit(List<DisposalPlanStep> steps) {
        if (steps == null || steps.isEmpty()) {
            return 0;
        }
        return steps.stream()
                .mapToInt(s -> s.getTimeLimitMinutes() != null ? s.getTimeLimitMinutes() : 0)
                .sum();
    }

    private void updateLinkProgress(Long linkId) {
        Long total = alarmPlanStepExecMapper.countTotalByLinkId(linkId);
        Long completed = alarmPlanStepExecMapper.countCompletedByLinkId(linkId);
        int totalInt = total != null ? total.intValue() : 0;
        int completedInt = completed != null ? completed.intValue() : 0;

        BigDecimal rate = BigDecimal.ZERO;
        if (totalInt > 0) {
            rate = BigDecimal.valueOf(completedInt)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalInt), 2, RoundingMode.HALF_UP);
        }

        AlarmPlanLink link = new AlarmPlanLink();
        link.setId(linkId);
        link.setCompletionRate(rate);
        updateById(link);
    }
}

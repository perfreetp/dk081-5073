package com.safetycampus.plan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.safetycampus.plan.dto.PlanStepExecDTO;
import com.safetycampus.plan.entity.AlarmPlanLink;
import com.safetycampus.plan.vo.AlarmPlanDetailVO;
import com.safetycampus.plan.vo.PlanMatchResultVO;

public interface AlarmPlanService extends IService<AlarmPlanLink> {

    PlanMatchResultVO matchAndLinkPlan(Long alarmId);

    AlarmPlanDetailVO getAlarmPlanDetail(Long alarmId);

    void startPlanExecution(Long linkId);

    void executeStep(PlanStepExecDTO dto);

    void skipStep(PlanStepExecDTO dto);

    void completePlan(Long linkId, String summary);

    void closeAlarmWithPlan(Long alarmId, Long linkId, String summary);
}

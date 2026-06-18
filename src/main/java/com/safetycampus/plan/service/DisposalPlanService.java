package com.safetycampus.plan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.safetycampus.plan.dto.DisposalPlanDTO;
import com.safetycampus.plan.dto.DisposalPlanQueryDTO;
import com.safetycampus.plan.entity.DisposalPlan;
import com.safetycampus.plan.entity.DisposalPlanStep;

import java.util.List;

public interface DisposalPlanService extends IService<DisposalPlan> {

    void createPlan(DisposalPlanDTO dto);

    void updatePlan(DisposalPlanDTO dto);

    void togglePlan(Long id, Boolean enabled);

    void deletePlan(Long id);

    DisposalPlan getPlanDetail(Long id);

    List<DisposalPlanStep> getPlanSteps(Long planId);

    IPage<DisposalPlan> selectPage(DisposalPlanQueryDTO queryDTO);
}

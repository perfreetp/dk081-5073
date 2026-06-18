package com.safetycampus.plan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.safetycampus.common.exception.BusinessException;
import com.safetycampus.common.result.ResultCode;
import com.safetycampus.plan.dto.DisposalPlanDTO;
import com.safetycampus.plan.dto.DisposalPlanQueryDTO;
import com.safetycampus.plan.entity.DisposalPlan;
import com.safetycampus.plan.entity.DisposalPlanStep;
import com.safetycampus.plan.mapper.DisposalPlanMapper;
import com.safetycampus.plan.mapper.DisposalPlanStepMapper;
import com.safetycampus.plan.service.DisposalPlanService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DisposalPlanServiceImpl extends ServiceImpl<DisposalPlanMapper, DisposalPlan> implements DisposalPlanService {

    @Resource
    private DisposalPlanStepMapper disposalPlanStepMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPlan(DisposalPlanDTO dto) {
        if (dto.getPlanName() == null || dto.getPlanName().trim().isEmpty()) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "planName");
        }
        if (dto.getAlarmLevel() == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "alarmLevel");
        }

        DisposalPlan plan = new DisposalPlan();
        plan.setPlanName(dto.getPlanName());
        plan.setPlanCode(dto.getPlanCode());
        plan.setAlarmLevel(dto.getAlarmLevel());
        plan.setDescription(dto.getDescription());
        plan.setIsEnabled(dto.getIsEnabled() != null ? dto.getIsEnabled() : 1);
        plan.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

        if (dto.getSchoolTypes() != null && dto.getSchoolTypes().length > 0) {
            String schoolTypesStr = Arrays.stream(dto.getSchoolTypes())
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            plan.setSchoolTypes(schoolTypesStr);
        }

        boolean saved = save(plan);
        if (!saved) {
            throw BusinessException.of(ResultCode.DATABASE_ERROR);
        }

        saveSteps(plan.getId(), dto.getSteps());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePlan(DisposalPlanDTO dto) {
        if (dto.getId() == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "id");
        }
        DisposalPlan existing = getById(dto.getId());
        if (existing == null) {
            throw BusinessException.of(ResultCode.PARAM_VALIDATE_ERROR);
        }

        DisposalPlan plan = new DisposalPlan();
        plan.setId(dto.getId());
        if (dto.getPlanName() != null) {
            plan.setPlanName(dto.getPlanName());
        }
        if (dto.getPlanCode() != null) {
            plan.setPlanCode(dto.getPlanCode());
        }
        if (dto.getAlarmLevel() != null) {
            plan.setAlarmLevel(dto.getAlarmLevel());
        }
        if (dto.getDescription() != null) {
            plan.setDescription(dto.getDescription());
        }
        if (dto.getIsEnabled() != null) {
            plan.setIsEnabled(dto.getIsEnabled());
        }
        if (dto.getSortOrder() != null) {
            plan.setSortOrder(dto.getSortOrder());
        }
        if (dto.getSchoolTypes() != null && dto.getSchoolTypes().length > 0) {
            String schoolTypesStr = Arrays.stream(dto.getSchoolTypes())
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            plan.setSchoolTypes(schoolTypesStr);
        }

        boolean updated = updateById(plan);
        if (!updated) {
            throw BusinessException.of(ResultCode.DATABASE_ERROR);
        }

        if (dto.getSteps() != null) {
            disposalPlanStepMapper.delete(
                    new LambdaQueryWrapper<DisposalPlanStep>()
                            .eq(DisposalPlanStep::getPlanId, dto.getId())
            );
            saveSteps(dto.getId(), dto.getSteps());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void togglePlan(Long id, Boolean enabled) {
        DisposalPlan plan = getById(id);
        if (plan == null) {
            throw BusinessException.of(ResultCode.PARAM_VALIDATE_ERROR);
        }
        plan.setIsEnabled(enabled ? 1 : 0);
        boolean updated = updateById(plan);
        if (!updated) {
            throw BusinessException.of(ResultCode.DATABASE_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePlan(Long id) {
        DisposalPlan plan = getById(id);
        if (plan == null) {
            throw BusinessException.of(ResultCode.PARAM_VALIDATE_ERROR);
        }
        boolean removed = removeById(id);
        if (!removed) {
            throw BusinessException.of(ResultCode.DATABASE_ERROR);
        }
        disposalPlanStepMapper.delete(
                new LambdaQueryWrapper<DisposalPlanStep>()
                        .eq(DisposalPlanStep::getPlanId, id)
        );
    }

    @Override
    public DisposalPlan getPlanDetail(Long id) {
        DisposalPlan plan = getById(id);
        if (plan == null) {
            throw BusinessException.of(ResultCode.PARAM_VALIDATE_ERROR);
        }
        return plan;
    }

    @Override
    public List<DisposalPlanStep> getPlanSteps(Long planId) {
        return disposalPlanStepMapper.selectByPlanId(planId);
    }

    @Override
    public IPage<DisposalPlan> selectPage(DisposalPlanQueryDTO queryDTO) {
        List<DisposalPlan> plans = baseMapper.selectPageByCondition(queryDTO);
        Long total = baseMapper.countByCondition(queryDTO);
        IPage<DisposalPlan> page = queryDTO.buildPage();
        page.setRecords(plans);
        page.setTotal(total != null ? total : 0);
        return page;
    }

    private void saveSteps(Long planId, List<DisposalPlanStep> steps) {
        if (steps == null || steps.isEmpty()) {
            return;
        }
        int stepNo = 1;
        for (DisposalPlanStep step : steps) {
            step.setPlanId(planId);
            if (step.getStepNo() == null) {
                step.setStepNo(stepNo++);
            }
            if (step.getIsRequired() == null) {
                step.setIsRequired(1);
            }
            if (step.getTimeLimitMinutes() == null) {
                step.setTimeLimitMinutes(0);
            }
            if (step.getSortOrder() == null) {
                step.setSortOrder(0);
            }
            disposalPlanStepMapper.insert(step);
        }
    }
}

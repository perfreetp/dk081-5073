package com.safetycampus.plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.safetycampus.plan.entity.DisposalPlanStep;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DisposalPlanStepMapper extends BaseMapper<DisposalPlanStep> {

    @Select("SELECT * FROM disposal_plan_step WHERE plan_id = #{planId} ORDER BY step_no ASC, sort_order ASC")
    List<DisposalPlanStep> selectByPlanId(@Param("planId") Long planId);
}

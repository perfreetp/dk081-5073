package com.safetycampus.plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.safetycampus.plan.dto.DisposalPlanQueryDTO;
import com.safetycampus.plan.entity.DisposalPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DisposalPlanMapper extends BaseMapper<DisposalPlan> {

    List<DisposalPlan> selectPageByCondition(@Param("query") DisposalPlanQueryDTO query);

    Long countByCondition(@Param("query") DisposalPlanQueryDTO query);
}

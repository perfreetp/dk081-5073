package com.safetycampus.plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.safetycampus.plan.entity.AlarmPlanStepExec;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AlarmPlanStepExecMapper extends BaseMapper<AlarmPlanStepExec> {

    @Select("SELECT * FROM alarm_plan_step_exec WHERE link_id = #{linkId} ORDER BY step_no ASC, id ASC")
    List<AlarmPlanStepExec> selectByLinkId(@Param("linkId") Long linkId);

    @Select("SELECT COUNT(*) FROM alarm_plan_step_exec WHERE link_id = #{linkId} AND exec_status IN (3,4)")
    Long countCompletedByLinkId(@Param("linkId") Long linkId);

    @Select("SELECT COUNT(*) FROM alarm_plan_step_exec WHERE link_id = #{linkId}")
    Long countTotalByLinkId(@Param("linkId") Long linkId);
}

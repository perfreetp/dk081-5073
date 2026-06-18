package com.safetycampus.plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.safetycampus.plan.entity.AlarmPlanLink;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AlarmPlanLinkMapper extends BaseMapper<AlarmPlanLink> {

    @Select("SELECT * FROM alarm_plan_link WHERE alarm_id = #{alarmId} ORDER BY created_at DESC LIMIT 1")
    AlarmPlanLink selectLatestByAlarmId(@Param("alarmId") Long alarmId);
}

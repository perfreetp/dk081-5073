package com.safetycampus.alarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.safetycampus.alarm.dto.AlarmTimelineQueryDTO;
import com.safetycampus.alarm.entity.AlarmFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlarmFlowMapper extends BaseMapper<AlarmFlow> {

    List<AlarmFlow> selectTimelineByQuery(@Param("queryDTO") AlarmTimelineQueryDTO queryDTO);
}

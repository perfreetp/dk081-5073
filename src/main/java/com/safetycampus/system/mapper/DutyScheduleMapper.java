package com.safetycampus.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.safetycampus.system.entity.DutySchedule;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface DutyScheduleMapper extends BaseMapper<DutySchedule> {

    List<DutySchedule> selectByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<DutySchedule> selectByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

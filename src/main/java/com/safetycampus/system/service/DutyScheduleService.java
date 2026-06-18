package com.safetycampus.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.safetycampus.system.dto.DutyScheduleDTO;
import com.safetycampus.system.entity.DutySchedule;

import java.time.LocalDate;
import java.util.List;

public interface DutyScheduleService extends IService<DutySchedule> {

    List<DutySchedule> getByDateRange(LocalDate startDate, LocalDate endDate);

    List<DutySchedule> getByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate);

    boolean saveSchedule(DutyScheduleDTO dto);

    boolean updateSchedule(DutyScheduleDTO dto);

    boolean deleteSchedule(Long id);

    boolean batchSave(List<DutyScheduleDTO> dtoList);

    DutySchedule getDetail(Long id);
}

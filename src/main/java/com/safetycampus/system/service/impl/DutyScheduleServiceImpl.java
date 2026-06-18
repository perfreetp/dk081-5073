package com.safetycampus.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.safetycampus.common.exception.BusinessException;
import com.safetycampus.system.dto.DutyScheduleDTO;
import com.safetycampus.system.entity.DutySchedule;
import com.safetycampus.system.entity.SysUser;
import com.safetycampus.system.mapper.DutyScheduleMapper;
import com.safetycampus.system.service.DutyScheduleService;
import com.safetycampus.system.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DutyScheduleServiceImpl extends ServiceImpl<DutyScheduleMapper, DutySchedule> implements DutyScheduleService {

    @Resource
    private SysUserService sysUserService;

    @Override
    public List<DutySchedule> getByDateRange(LocalDate startDate, LocalDate endDate) {
        return baseMapper.selectByDateRange(startDate, endDate);
    }

    @Override
    public List<DutySchedule> getByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return baseMapper.selectByUserIdAndDateRange(userId, startDate, endDate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveSchedule(DutyScheduleDTO dto) {
        SysUser user = sysUserService.getById(dto.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        DutySchedule schedule = new DutySchedule();
        BeanUtils.copyProperties(dto, schedule);
        schedule.setUserName(user.getRealName());

        if (schedule.getIsStandby() == null) {
            schedule.setIsStandby(0);
        }

        return this.save(schedule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSchedule(DutyScheduleDTO dto) {
        DutySchedule schedule = new DutySchedule();
        BeanUtils.copyProperties(dto, schedule);

        if (dto.getUserId() != null) {
            SysUser user = sysUserService.getById(dto.getUserId());
            if (user != null) {
                schedule.setUserName(user.getRealName());
            }
        }

        return this.updateById(schedule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSchedule(Long id) {
        return this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSave(List<DutyScheduleDTO> dtoList) {
        List<DutySchedule> scheduleList = new ArrayList<>();
        for (DutyScheduleDTO dto : dtoList) {
            SysUser user = sysUserService.getById(dto.getUserId());
            if (user == null) {
                throw new BusinessException("用户ID " + dto.getUserId() + " 不存在");
            }

            DutySchedule schedule = new DutySchedule();
            BeanUtils.copyProperties(dto, schedule);
            schedule.setUserName(user.getRealName());

            if (schedule.getIsStandby() == null) {
                schedule.setIsStandby(0);
            }

            scheduleList.add(schedule);
        }
        return this.saveBatch(scheduleList);
    }

    @Override
    public DutySchedule getDetail(Long id) {
        return this.getById(id);
    }
}

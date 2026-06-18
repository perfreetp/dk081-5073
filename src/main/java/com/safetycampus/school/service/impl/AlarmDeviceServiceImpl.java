package com.safetycampus.school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.safetycampus.common.exception.BusinessException;
import com.safetycampus.common.result.ResultCode;
import com.safetycampus.school.dto.DeviceBindDTO;
import com.safetycampus.school.entity.AlarmDevice;
import com.safetycampus.school.entity.SchoolInfo;
import com.safetycampus.school.mapper.AlarmDeviceMapper;
import com.safetycampus.school.mapper.SchoolInfoMapper;
import com.safetycampus.school.service.AlarmDeviceService;
import com.safetycampus.school.service.SchoolInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlarmDeviceServiceImpl extends ServiceImpl<AlarmDeviceMapper, AlarmDevice> implements AlarmDeviceService {

    @Autowired
    private SchoolInfoService schoolInfoService;

    @Override
    public IPage<AlarmDevice> selectPage(Long schoolId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<AlarmDevice> wrapper = new LambdaQueryWrapper<>();
        if (schoolId != null) {
            wrapper.eq(AlarmDevice::getSchoolId, schoolId);
        }
        wrapper.orderByDesc(AlarmDevice::getId);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<AlarmDevice> listBySchoolId(Long schoolId) {
        LambdaQueryWrapper<AlarmDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmDevice::getSchoolId, schoolId);
        wrapper.orderByDesc(AlarmDevice::getId);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindDevice(DeviceBindDTO dto) {
        if (dto.getDeviceCode() != null) {
            LambdaQueryWrapper<AlarmDevice> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AlarmDevice::getDeviceCode, dto.getDeviceCode());
            Long count = this.count(wrapper);
            if (count > 0) {
                throw BusinessException.of(ResultCode.DEVICE_CODE_EXISTS);
            }
        }
        if (dto.getSchoolId() != null) {
            SchoolInfo school = schoolInfoService.getById(dto.getSchoolId());
            if (school == null) {
                throw BusinessException.of(ResultCode.SCHOOL_NOT_FOUND);
            }
            if (school.getStatus() != null && school.getStatus() == 0) {
                throw BusinessException.of(ResultCode.SCHOOL_DISABLED);
            }
        }
        AlarmDevice device = new AlarmDevice();
        BeanUtils.copyProperties(dto, device);
        if (device.getStatus() == null) {
            device.setStatus(1);
        }
        if (device.getLastOnlineAt() == null) {
            device.setLastOnlineAt(LocalDateTime.now());
        }
        boolean saved = this.save(device);
        if (!saved) {
            throw BusinessException.of(ResultCode.DATABASE_ERROR);
        }
        if (dto.getSchoolId() != null) {
            schoolInfoService.updateDeviceCount(dto.getSchoolId(), 1);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDevice(DeviceBindDTO dto) {
        AlarmDevice oldDevice = this.getById(dto.getId());
        if (oldDevice == null) {
            throw BusinessException.of(ResultCode.DEVICE_NOT_FOUND);
        }
        if (dto.getDeviceCode() != null && !dto.getDeviceCode().equals(oldDevice.getDeviceCode())) {
            LambdaQueryWrapper<AlarmDevice> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AlarmDevice::getDeviceCode, dto.getDeviceCode());
            wrapper.ne(AlarmDevice::getId, dto.getId());
            Long count = this.count(wrapper);
            if (count > 0) {
                throw BusinessException.of(ResultCode.DEVICE_CODE_EXISTS);
            }
        }
        if (dto.getSchoolId() != null && !dto.getSchoolId().equals(oldDevice.getSchoolId())) {
            SchoolInfo school = schoolInfoService.getById(dto.getSchoolId());
            if (school == null) {
                throw BusinessException.of(ResultCode.SCHOOL_NOT_FOUND);
            }
            if (school.getStatus() != null && school.getStatus() == 0) {
                throw BusinessException.of(ResultCode.SCHOOL_DISABLED);
            }
        }
        Long oldSchoolId = oldDevice.getSchoolId();
        Long newSchoolId = dto.getSchoolId();

        AlarmDevice device = new AlarmDevice();
        BeanUtils.copyProperties(dto, device);
        boolean updated = this.updateById(device);
        if (!updated) {
            throw BusinessException.of(ResultCode.DATABASE_ERROR);
        }

        if (oldSchoolId != null && !oldSchoolId.equals(newSchoolId)) {
            schoolInfoService.updateDeviceCount(oldSchoolId, -1);
            if (newSchoolId != null) {
                schoolInfoService.updateDeviceCount(newSchoolId, 1);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindDevice(Long id) {
        AlarmDevice device = this.getById(id);
        if (device == null) {
            throw BusinessException.of(ResultCode.DEVICE_NOT_FOUND);
        }
        boolean removed = this.removeById(id);
        if (!removed) {
            throw BusinessException.of(ResultCode.DATABASE_ERROR);
        }
        if (device.getSchoolId() != null) {
            schoolInfoService.updateDeviceCount(device.getSchoolId(), -1);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDeviceStatus(String deviceCode, Integer status) {
        LambdaQueryWrapper<AlarmDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmDevice::getDeviceCode, deviceCode);
        AlarmDevice device = this.getOne(wrapper);
        if (device == null) {
            throw BusinessException.of(ResultCode.DEVICE_NOT_FOUND);
        }
        device.setStatus(status);
        if (status == 1) {
            device.setLastOnlineAt(LocalDateTime.now());
        } else if (status == 0) {
            throw BusinessException.of(ResultCode.DEVICE_OFFLINE);
        }
        boolean updated = this.updateById(device);
        if (!updated) {
            throw BusinessException.of(ResultCode.DATABASE_ERROR);
        }
    }
}

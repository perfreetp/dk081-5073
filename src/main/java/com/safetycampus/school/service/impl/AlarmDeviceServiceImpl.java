package com.safetycampus.school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.safetycampus.school.dto.DeviceBindDTO;
import com.safetycampus.school.entity.AlarmDevice;
import com.safetycampus.school.mapper.AlarmDeviceMapper;
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
    public boolean bindDevice(DeviceBindDTO dto) {
        AlarmDevice device = new AlarmDevice();
        BeanUtils.copyProperties(dto, device);
        if (device.getStatus() == null) {
            device.setStatus(1);
        }
        if (device.getLastOnlineAt() == null) {
            device.setLastOnlineAt(LocalDateTime.now());
        }
        boolean result = this.save(device);
        if (result && dto.getSchoolId() != null) {
            schoolInfoService.updateDeviceCount(dto.getSchoolId(), 1);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDevice(DeviceBindDTO dto) {
        AlarmDevice oldDevice = this.getById(dto.getId());
        if (oldDevice == null) {
            return false;
        }
        Long oldSchoolId = oldDevice.getSchoolId();
        Long newSchoolId = dto.getSchoolId();

        AlarmDevice device = new AlarmDevice();
        BeanUtils.copyProperties(dto, device);
        boolean result = this.updateById(device);

        if (result && oldSchoolId != null && !oldSchoolId.equals(newSchoolId)) {
            schoolInfoService.updateDeviceCount(oldSchoolId, -1);
            if (newSchoolId != null) {
                schoolInfoService.updateDeviceCount(newSchoolId, 1);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unbindDevice(Long id) {
        AlarmDevice device = this.getById(id);
        if (device == null) {
            return false;
        }
        boolean result = this.removeById(id);
        if (result && device.getSchoolId() != null) {
            schoolInfoService.updateDeviceCount(device.getSchoolId(), -1);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDeviceStatus(String deviceCode, Integer status) {
        LambdaQueryWrapper<AlarmDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmDevice::getDeviceCode, deviceCode);
        AlarmDevice device = this.getOne(wrapper);
        if (device != null) {
            device.setStatus(status);
            if (status == 1) {
                device.setLastOnlineAt(LocalDateTime.now());
            }
            return this.updateById(device);
        }
        return false;
    }
}

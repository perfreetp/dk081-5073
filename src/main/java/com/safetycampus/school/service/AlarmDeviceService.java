package com.safetycampus.school.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.safetycampus.school.dto.DeviceBindDTO;
import com.safetycampus.school.entity.AlarmDevice;

import java.util.List;

public interface AlarmDeviceService extends IService<AlarmDevice> {

    IPage<AlarmDevice> selectPage(Long schoolId, Integer pageNum, Integer pageSize);

    List<AlarmDevice> listBySchoolId(Long schoolId);

    boolean bindDevice(DeviceBindDTO dto);

    boolean updateDevice(DeviceBindDTO dto);

    boolean unbindDevice(Long id);

    boolean updateDeviceStatus(String deviceCode, Integer status);
}

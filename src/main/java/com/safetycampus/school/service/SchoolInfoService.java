package com.safetycampus.school.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.safetycampus.school.dto.SchoolInfoDTO;
import com.safetycampus.school.dto.SchoolInfoQueryDTO;
import com.safetycampus.school.entity.SchoolInfo;

import java.util.List;
import java.util.Map;

public interface SchoolInfoService extends IService<SchoolInfo> {

    IPage<SchoolInfo> selectPage(SchoolInfoQueryDTO queryDTO);

    void addSchool(SchoolInfoDTO dto);

    void updateSchool(SchoolInfoDTO dto);

    void deleteSchool(Long id);

    SchoolInfo getSchoolDetail(Long id);

    Map<Integer, List<SchoolInfo>> groupByType();

    Map<Long, List<SchoolInfo>> groupByGroup();

    boolean updateDeviceCount(Long schoolId, int count);
}

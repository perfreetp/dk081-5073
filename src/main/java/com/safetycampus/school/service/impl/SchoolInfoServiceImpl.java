package com.safetycampus.school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.safetycampus.school.dto.SchoolInfoDTO;
import com.safetycampus.school.dto.SchoolInfoQueryDTO;
import com.safetycampus.school.entity.SchoolInfo;
import com.safetycampus.school.mapper.SchoolInfoMapper;
import com.safetycampus.school.service.SchoolInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SchoolInfoServiceImpl extends ServiceImpl<SchoolInfoMapper, SchoolInfo> implements SchoolInfoService {

    @Override
    public IPage<SchoolInfo> selectPage(SchoolInfoQueryDTO queryDTO) {
        LambdaQueryWrapper<SchoolInfo> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO.getSchoolCode() != null && !queryDTO.getSchoolCode().isEmpty()) {
            wrapper.like(SchoolInfo::getSchoolCode, queryDTO.getSchoolCode());
        }
        if (queryDTO.getSchoolName() != null && !queryDTO.getSchoolName().isEmpty()) {
            wrapper.like(SchoolInfo::getSchoolName, queryDTO.getSchoolName());
        }
        if (queryDTO.getSchoolType() != null) {
            wrapper.eq(SchoolInfo::getSchoolType, queryDTO.getSchoolType());
        }
        if (queryDTO.getSchoolLevel() != null) {
            wrapper.eq(SchoolInfo::getSchoolLevel, queryDTO.getSchoolLevel());
        }
        if (queryDTO.getGroupId() != null) {
            wrapper.eq(SchoolInfo::getGroupId, queryDTO.getGroupId());
        }
        if (queryDTO.getPoliceStationId() != null) {
            wrapper.eq(SchoolInfo::getPoliceStationId, queryDTO.getPoliceStationId());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(SchoolInfo::getStatus, queryDTO.getStatus());
        }
        return this.page(queryDTO.buildPage(), wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addSchool(SchoolInfoDTO dto) {
        SchoolInfo school = new SchoolInfo();
        BeanUtils.copyProperties(dto, school);
        if (school.getDeviceCount() == null) {
            school.setDeviceCount(0);
        }
        if (school.getStudentCount() == null) {
            school.setStudentCount(0);
        }
        if (school.getTeacherCount() == null) {
            school.setTeacherCount(0);
        }
        if (school.getSchoolLevel() == null) {
            school.setSchoolLevel(3);
        }
        if (school.getStatus() == null) {
            school.setStatus(1);
        }
        return this.save(school);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSchool(SchoolInfoDTO dto) {
        SchoolInfo school = new SchoolInfo();
        BeanUtils.copyProperties(dto, school);
        return this.updateById(school);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSchool(Long id) {
        return this.removeById(id);
    }

    @Override
    public SchoolInfo getSchoolDetail(Long id) {
        return this.getById(id);
    }

    @Override
    public Map<Integer, List<SchoolInfo>> groupByType() {
        List<SchoolInfo> list = this.list();
        return list.stream().collect(Collectors.groupingBy(SchoolInfo::getSchoolType));
    }

    @Override
    public Map<Long, List<SchoolInfo>> groupByGroup() {
        List<SchoolInfo> list = this.list();
        return list.stream().collect(Collectors.groupingBy(
                school -> school.getGroupId() != null ? school.getGroupId() : 0L
        ));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDeviceCount(Long schoolId, int count) {
        SchoolInfo school = this.getById(schoolId);
        if (school != null) {
            int newCount = Math.max(0, (school.getDeviceCount() != null ? school.getDeviceCount() : 0) + count);
            school.setDeviceCount(newCount);
            return this.updateById(school);
        }
        return false;
    }
}

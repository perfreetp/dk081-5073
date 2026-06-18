package com.safetycampus.school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.safetycampus.school.entity.SchoolGroup;
import com.safetycampus.school.mapper.SchoolGroupMapper;
import com.safetycampus.school.service.SchoolGroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SchoolGroupServiceImpl extends ServiceImpl<SchoolGroupMapper, SchoolGroup> implements SchoolGroupService {

    @Override
    public List<SchoolGroup> listAll() {
        LambdaQueryWrapper<SchoolGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SchoolGroup::getSortOrder);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addGroup(SchoolGroup group) {
        if (group.getSortOrder() == null) {
            group.setSortOrder(0);
        }
        return this.save(group);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateGroup(SchoolGroup group) {
        return this.updateById(group);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteGroup(Long id) {
        return this.removeById(id);
    }
}

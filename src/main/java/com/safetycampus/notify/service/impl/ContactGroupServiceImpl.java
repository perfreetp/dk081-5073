package com.safetycampus.notify.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.safetycampus.common.exception.BusinessException;
import com.safetycampus.notify.entity.ContactGroup;
import com.safetycampus.notify.mapper.ContactGroupMapper;
import com.safetycampus.notify.service.ContactGroupService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactGroupServiceImpl extends ServiceImpl<ContactGroupMapper, ContactGroup> implements ContactGroupService {

    @Resource
    private ContactGroupMapper contactGroupMapper;

    @Override
    public boolean add(ContactGroup group) {
        LambdaQueryWrapper<ContactGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContactGroup::getGroupName, group.getGroupName());
        if (count(wrapper) > 0) {
            throw new BusinessException("分组名称已存在");
        }
        if (group.getSortOrder() == null) {
            group.setSortOrder(0);
        }
        return save(group);
    }

    @Override
    public boolean update(ContactGroup group) {
        ContactGroup exist = getById(group.getId());
        if (exist == null) {
            throw new BusinessException("分组不存在");
        }
        if (!exist.getGroupName().equals(group.getGroupName())) {
            LambdaQueryWrapper<ContactGroup> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ContactGroup::getGroupName, group.getGroupName());
            if (count(wrapper) > 0) {
                throw new BusinessException("分组名称已存在");
            }
        }
        return updateById(group);
    }

    @Override
    public boolean delete(Long id) {
        ContactGroup group = getById(id);
        if (group == null) {
            throw new BusinessException("分组不存在");
        }
        return removeById(id);
    }

    @Override
    public List<ContactGroup> listAll() {
        LambdaQueryWrapper<ContactGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(ContactGroup::getSortOrder).orderByDesc(ContactGroup::getId);
        return list(wrapper);
    }
}

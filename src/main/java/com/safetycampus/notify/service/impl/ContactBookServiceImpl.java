package com.safetycampus.notify.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.safetycampus.common.exception.BusinessException;
import com.safetycampus.notify.dto.ContactBookDTO;
import com.safetycampus.notify.entity.ContactBook;
import com.safetycampus.notify.mapper.ContactBookMapper;
import com.safetycampus.notify.service.ContactBookService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ContactBookServiceImpl extends ServiceImpl<ContactBookMapper, ContactBook> implements ContactBookService {

    @Resource
    private ContactBookMapper contactBookMapper;

    @Override
    public IPage<ContactBook> selectPage(ContactBookDTO dto) {
        LambdaQueryWrapper<ContactBook> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(w -> w.like(ContactBook::getName, dto.getKeyword())
                    .or().like(ContactBook::getPhone, dto.getKeyword())
                    .or().like(ContactBook::getUnitName, dto.getKeyword()));
        }
        if (dto.getUnitType() != null) {
            wrapper.eq(ContactBook::getUnitType, dto.getUnitType());
        }
        if (dto.getGroupId() != null) {
            wrapper.eq(ContactBook::getGroupId, dto.getGroupId());
        }
        if (dto.getIsDuty() != null) {
            wrapper.eq(ContactBook::getIsDuty, dto.getIsDuty());
        }
        wrapper.orderByAsc(ContactBook::getSortOrder).orderByDesc(ContactBook::getId);
        return page(dto.buildPage(), wrapper);
    }

    @Override
    public boolean add(ContactBookDTO dto) {
        ContactBook contact = new ContactBook();
        contact.setName(dto.getName());
        contact.setPhone(dto.getPhone());
        contact.setUnitType(dto.getUnitType());
        contact.setUnitName(dto.getUnitName());
        contact.setPosition(dto.getPosition());
        contact.setGroupId(dto.getGroupId());
        contact.setIsDuty(dto.getIsDuty() != null ? dto.getIsDuty() : 0);
        contact.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        return save(contact);
    }

    @Override
    public boolean update(ContactBookDTO dto) {
        ContactBook contact = getById(dto.getId());
        if (contact == null) {
            throw new BusinessException("联系人不存在");
        }
        contact.setName(dto.getName());
        contact.setPhone(dto.getPhone());
        contact.setUnitType(dto.getUnitType());
        contact.setUnitName(dto.getUnitName());
        contact.setPosition(dto.getPosition());
        contact.setGroupId(dto.getGroupId());
        contact.setIsDuty(dto.getIsDuty() != null ? dto.getIsDuty() : contact.getIsDuty());
        contact.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : contact.getSortOrder());
        return updateById(contact);
    }

    @Override
    public boolean delete(Long id) {
        ContactBook contact = getById(id);
        if (contact == null) {
            throw new BusinessException("联系人不存在");
        }
        return removeById(id);
    }

    @Override
    public ContactBook getDetail(Long id) {
        return getById(id);
    }

    @Override
    public List<ContactBook> listByGroupId(Long groupId) {
        LambdaQueryWrapper<ContactBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContactBook::getGroupId, groupId);
        wrapper.orderByAsc(ContactBook::getSortOrder).orderByDesc(ContactBook::getId);
        return list(wrapper);
    }

    @Override
    public List<ContactBook> listByUnitType(Integer unitType) {
        LambdaQueryWrapper<ContactBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContactBook::getUnitType, unitType);
        wrapper.orderByAsc(ContactBook::getSortOrder).orderByDesc(ContactBook::getId);
        return list(wrapper);
    }

    @Override
    public List<ContactBook> listDutyContacts() {
        LambdaQueryWrapper<ContactBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContactBook::getIsDuty, 1);
        wrapper.orderByAsc(ContactBook::getSortOrder).orderByDesc(ContactBook::getId);
        return list(wrapper);
    }
}

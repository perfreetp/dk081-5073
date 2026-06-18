package com.safetycampus.notify.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.safetycampus.notify.dto.ContactBookDTO;
import com.safetycampus.notify.entity.ContactBook;

import java.util.List;

public interface ContactBookService extends IService<ContactBook> {

    IPage<ContactBook> selectPage(ContactBookDTO dto);

    boolean add(ContactBookDTO dto);

    boolean update(ContactBookDTO dto);

    boolean delete(Long id);

    ContactBook getDetail(Long id);

    List<ContactBook> listByGroupId(Long groupId);

    List<ContactBook> listByUnitType(Integer unitType);

    List<ContactBook> listDutyContacts();
}

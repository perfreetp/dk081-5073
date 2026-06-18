package com.safetycampus.notify.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.safetycampus.notify.entity.ContactGroup;

import java.util.List;

public interface ContactGroupService extends IService<ContactGroup> {

    boolean add(ContactGroup group);

    boolean update(ContactGroup group);

    boolean delete(Long id);

    List<ContactGroup> listAll();
}

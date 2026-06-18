package com.safetycampus.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.safetycampus.school.entity.SchoolGroup;

import java.util.List;

public interface SchoolGroupService extends IService<SchoolGroup> {

    List<SchoolGroup> listAll();

    boolean addGroup(SchoolGroup group);

    boolean updateGroup(SchoolGroup group);

    boolean deleteGroup(Long id);
}

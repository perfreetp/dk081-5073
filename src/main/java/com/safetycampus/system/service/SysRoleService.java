package com.safetycampus.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.safetycampus.system.entity.SysRole;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {

    List<SysRole> getAllRoles();

    SysRole getByCode(String roleCode);

    boolean saveRole(SysRole role);

    boolean updateRole(SysRole role);

    boolean deleteRole(Long id);
}

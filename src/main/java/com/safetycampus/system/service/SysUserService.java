package com.safetycampus.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.safetycampus.system.dto.SysUserDTO;
import com.safetycampus.system.dto.SysUserQueryDTO;
import com.safetycampus.system.entity.SysUser;

public interface SysUserService extends IService<SysUser> {

    IPage<SysUser> selectPage(SysUserQueryDTO queryDTO);

    SysUser getByUsername(String username);

    boolean saveUser(SysUserDTO dto);

    boolean updateUser(SysUserDTO dto);

    boolean deleteUser(Long id);

    boolean resetPassword(Long id, String newPassword);

    SysUser getDetail(Long id);
}

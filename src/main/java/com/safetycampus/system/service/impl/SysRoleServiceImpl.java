package com.safetycampus.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.safetycampus.common.exception.BusinessException;
import com.safetycampus.system.entity.SysRole;
import com.safetycampus.system.mapper.SysRoleMapper;
import com.safetycampus.system.service.SysRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Override
    public List<SysRole> getAllRoles() {
        return this.list();
    }

    @Override
    public SysRole getByCode(String roleCode) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, roleCode);
        return this.getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveRole(SysRole role) {
        SysRole existRole = getByCode(role.getRoleCode());
        if (existRole != null) {
            throw new BusinessException("角色编码已存在");
        }
        return this.save(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(SysRole role) {
        return this.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long id) {
        return this.removeById(id);
    }
}

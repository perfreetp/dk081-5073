package com.safetycampus.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.safetycampus.common.exception.BusinessException;
import com.safetycampus.system.dto.SysUserDTO;
import com.safetycampus.system.dto.SysUserQueryDTO;
import com.safetycampus.system.entity.SysUser;
import com.safetycampus.system.mapper.SysUserMapper;
import com.safetycampus.system.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public IPage<SysUser> selectPage(SysUserQueryDTO queryDTO) {
        return baseMapper.selectPageByCondition(queryDTO.buildPage(), queryDTO);
    }

    @Override
    public SysUser getByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveUser(SysUserDTO dto) {
        SysUser existUser = getByUsername(dto.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);

        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new BusinessException("密码不能为空");
        }
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        return this.save(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(SysUserDTO dto) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else {
            user.setPassword(null);
        }

        return this.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long id) {
        return this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(Long id, String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new BusinessException("新密码不能为空");
        }
        SysUser user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        return this.updateById(user);
    }

    @Override
    public SysUser getDetail(Long id) {
        return this.getById(id);
    }
}

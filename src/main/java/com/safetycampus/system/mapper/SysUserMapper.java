package com.safetycampus.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.safetycampus.system.dto.SysUserQueryDTO;
import com.safetycampus.system.entity.SysUser;
import org.apache.ibatis.annotations.Param;

public interface SysUserMapper extends BaseMapper<SysUser> {

    SysUser selectByUsername(@Param("username") String username);

    IPage<SysUser> selectPageByCondition(IPage<SysUser> page, @Param("query") SysUserQueryDTO queryDTO);
}

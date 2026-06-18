package com.safetycampus.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.safetycampus.system.entity.SysParam;
import org.apache.ibatis.annotations.Param;

public interface SysParamMapper extends BaseMapper<SysParam> {

    SysParam selectByParamKey(@Param("paramKey") String paramKey);
}

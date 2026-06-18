package com.safetycampus.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.safetycampus.system.dto.SysParamDTO;
import com.safetycampus.system.entity.SysParam;

public interface SysParamService extends IService<SysParam> {

    IPage<SysParam> selectPage(String keyword, Integer pageNum, Integer pageSize);

    SysParam getByParamKey(String paramKey);

    String getValueByKey(String paramKey);

    boolean saveParam(SysParamDTO dto);

    boolean updateParam(SysParamDTO dto);

    boolean deleteParam(Long id);

    boolean refreshCache();
}

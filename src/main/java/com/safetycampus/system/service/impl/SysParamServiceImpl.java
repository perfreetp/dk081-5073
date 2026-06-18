package com.safetycampus.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.safetycampus.common.exception.BusinessException;
import com.safetycampus.system.dto.SysParamDTO;
import com.safetycampus.system.entity.SysParam;
import com.safetycampus.system.mapper.SysParamMapper;
import com.safetycampus.system.service.SysParamService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SysParamServiceImpl extends ServiceImpl<SysParamMapper, SysParam> implements SysParamService {

    private final Map<String, String> paramCache = new ConcurrentHashMap<>();

    @Override
    public IPage<SysParam> selectPage(String keyword, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<SysParam> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(SysParam::getParamKey, keyword)
                    .or()
                    .like(SysParam::getParamName, keyword);
        }
        wrapper.orderByDesc(SysParam::getId);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public SysParam getByParamKey(String paramKey) {
        return baseMapper.selectByParamKey(paramKey);
    }

    @Override
    public String getValueByKey(String paramKey) {
        String value = paramCache.get(paramKey);
        if (value == null) {
            SysParam param = getByParamKey(paramKey);
            if (param != null) {
                value = param.getParamValue();
                paramCache.put(paramKey, value);
            }
        }
        return value;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveParam(SysParamDTO dto) {
        SysParam existParam = getByParamKey(dto.getParamKey());
        if (existParam != null) {
            throw new BusinessException("参数键已存在");
        }

        SysParam param = new SysParam();
        BeanUtils.copyProperties(dto, param);
        boolean result = this.save(param);

        if (result) {
            paramCache.put(param.getParamKey(), param.getParamValue());
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateParam(SysParamDTO dto) {
        SysParam param = new SysParam();
        BeanUtils.copyProperties(dto, param);
        boolean result = this.updateById(param);

        if (result) {
            SysParam updatedParam = this.getById(dto.getId());
            if (updatedParam != null) {
                paramCache.put(updatedParam.getParamKey(), updatedParam.getParamValue());
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteParam(Long id) {
        SysParam param = this.getById(id);
        if (param != null) {
            paramCache.remove(param.getParamKey());
        }
        return this.removeById(id);
    }

    @Override
    public boolean refreshCache() {
        paramCache.clear();
        List<SysParam> list = this.list();
        for (SysParam param : list) {
            paramCache.put(param.getParamKey(), param.getParamValue());
        }
        return true;
    }
}

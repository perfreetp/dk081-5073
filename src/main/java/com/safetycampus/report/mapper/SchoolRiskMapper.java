package com.safetycampus.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.safetycampus.report.dto.RiskQueryDTO;
import com.safetycampus.report.entity.SchoolRisk;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SchoolRiskMapper extends BaseMapper<SchoolRisk> {

    IPage<SchoolRisk> selectPageByCondition(IPage<SchoolRisk> page, @Param("query") RiskQueryDTO queryDTO);

    List<SchoolRisk> selectHighRiskSchools(@Param("riskLevel") Integer riskLevel, @Param("statMonth") String statMonth);
}

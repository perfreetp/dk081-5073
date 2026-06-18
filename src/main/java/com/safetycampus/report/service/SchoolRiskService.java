package com.safetycampus.report.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.safetycampus.report.dto.RiskQueryDTO;
import com.safetycampus.report.entity.SchoolRisk;

import java.math.BigDecimal;
import java.util.List;

public interface SchoolRiskService extends IService<SchoolRisk> {

    BigDecimal calculateRiskScore(Long schoolId, String statMonth);

    SchoolRisk generateRiskPortrait(Long schoolId, String statMonth);

    List<SchoolRisk> getHighRiskSchools(String statMonth);

    IPage<SchoolRisk> selectPage(RiskQueryDTO queryDTO);

    SchoolRisk getDetail(Long id);
}

package com.safetycampus.report.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.safetycampus.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("school_risk")
public class SchoolRisk extends BaseEntity {

    private Long schoolId;

    private String statMonth;

    private Integer totalAlarms;

    private Integer criticalAlarms;

    private Integer avgResponseTime;

    private Integer avgHandleTime;

    private Integer timeoutCount;

    private Integer schoolLevel;

    private BigDecimal riskScore;

    private Integer riskLevel;

    private String indicators;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate statDate;
}

package com.safetycampus.report.dto;

import com.safetycampus.common.entity.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class RiskQueryDTO extends PageQuery implements Serializable {

    private Long schoolId;

    private Long groupId;

    private String statMonth;

    private Integer riskLevel;

    private Integer schoolLevel;

    private String keyword;
}

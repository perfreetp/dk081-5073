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
@TableName("assess_record")
public class AssessRecord extends BaseEntity {

    private Long schoolId;

    private String statQuarter;

    private BigDecimal totalScore;

    private BigDecimal alarmScore;

    private BigDecimal responseScore;

    private BigDecimal handleScore;

    private BigDecimal dutyScore;

    private Integer rankNum;

    private String grade;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate assessDate;
}

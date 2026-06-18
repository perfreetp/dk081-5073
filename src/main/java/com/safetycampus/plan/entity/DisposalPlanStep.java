package com.safetycampus.plan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.safetycampus.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("disposal_plan_step")
@Schema(description = "处置预案步骤")
public class DisposalPlanStep extends BaseEntity {

    @Schema(description = "预案ID")
    private Long planId;

    @Schema(description = "步骤序号")
    private Integer stepNo;

    @Schema(description = "步骤名称")
    private String stepName;

    @Schema(description = "步骤内容")
    private String stepContent;

    @Schema(description = "责任单位")
    private String responsibleUnit;

    @Schema(description = "时限(分钟)")
    private Integer timeLimitMinutes;

    @Schema(description = "是否必填:0-否,1-是")
    private Integer isRequired;

    @Schema(description = "通知角色,逗号分隔")
    private String noticeRoles;

    @Schema(description = "排序")
    private Integer sortOrder;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}

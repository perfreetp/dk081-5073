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
@TableName("alarm_plan_step_exec")
@Schema(description = "预案步骤执行")
public class AlarmPlanStepExec extends BaseEntity {

    @Schema(description = "关联ID")
    private Long linkId;

    @Schema(description = "警情ID")
    private Long alarmId;

    @Schema(description = "预案步骤ID")
    private Long planStepId;

    @Schema(description = "步骤序号")
    private Integer stepNo;

    @Schema(description = "步骤名称")
    private String stepName;

    @Schema(description = "责任单位")
    private String responsibleUnit;

    @Schema(description = "时限(分钟)")
    private Integer timeLimitMinutes;

    @Schema(description = "执行状态:1-未开始,2-进行中,3-已完成,4-已跳过")
    private Integer execStatus;

    @Schema(description = "执行人ID")
    private Long executedBy;

    @Schema(description = "执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime executedAt;

    @Schema(description = "耗时(秒)")
    private Integer durationSeconds;

    @Schema(description = "执行备注")
    private String execRemark;

    @Schema(description = "附件URL")
    private String attachUrl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

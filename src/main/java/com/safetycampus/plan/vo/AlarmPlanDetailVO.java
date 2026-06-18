package com.safetycampus.plan.vo;

import com.safetycampus.plan.entity.AlarmPlanLink;
import com.safetycampus.plan.entity.AlarmPlanStepExec;
import com.safetycampus.plan.entity.DisposalPlan;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "警情关联预案详情")
public class AlarmPlanDetailVO {

    @Schema(description = "预案基本信息")
    private DisposalPlan plan;

    @Schema(description = "警情预案关联信息")
    private AlarmPlanLink link;

    @Schema(description = "总步骤数")
    private Integer totalSteps;

    @Schema(description = "已完成步骤数")
    private Integer completedSteps;

    @Schema(description = "总体进度描述(如: 3/5)")
    private String progressText;

    @Schema(description = "完成率百分比")
    private BigDecimal completionRate;

    @Schema(description = "步骤执行详情列表")
    private List<AlarmPlanStepExec> stepExecList;
}

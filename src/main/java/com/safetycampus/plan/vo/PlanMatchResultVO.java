package com.safetycampus.plan.vo;

import com.safetycampus.plan.entity.DisposalPlan;
import com.safetycampus.plan.entity.DisposalPlanStep;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "预案匹配结果")
public class PlanMatchResultVO {

    @Schema(description = "匹配到的预案基本信息")
    private DisposalPlan plan;

    @Schema(description = "预案步骤列表")
    private List<DisposalPlanStep> steps;

    @Schema(description = "建议总时限(分钟)")
    private Integer suggestedTimeLimit;

    @Schema(description = "匹配原因说明")
    private String matchReason;
}

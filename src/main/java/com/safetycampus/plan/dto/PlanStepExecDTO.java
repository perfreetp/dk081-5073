package com.safetycampus.plan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "步骤执行回填DTO")
public class PlanStepExecDTO {

    @Schema(description = "关联ID")
    @NotNull(message = "关联ID不能为空")
    private Long linkId;

    @Schema(description = "步骤执行ID")
    @NotNull(message = "步骤ID不能为空")
    private Long stepId;

    @Schema(description = "执行状态:2-进行中,3-已完成")
    private Integer execStatus;

    @Schema(description = "执行备注")
    private String execRemark;

    @Schema(description = "附件URL")
    private String attachUrl;

    @Schema(description = "执行人ID")
    private Long executedBy;
}

package com.safetycampus.plan.dto;

import com.safetycampus.plan.entity.DisposalPlanStep;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "处置预案创建/编辑DTO")
public class DisposalPlanDTO {

    @Schema(description = "预案ID")
    private Long id;

    @Schema(description = "预案名称")
    @NotBlank(message = "预案名称不能为空")
    private String planName;

    @Schema(description = "预案编码")
    private String planCode;

    @Schema(description = "适用警情级别:1-重大,2-较大,3-一般")
    @NotNull(message = "警情级别不能为空")
    private Integer alarmLevel;

    @Schema(description = "适用学校类型数组:1-幼儿园,2-小学,3-初中,4-高中,5-中职,6-高校")
    private Integer[] schoolTypes;

    @Schema(description = "预案描述")
    private String description;

    @Schema(description = "是否启用:0-禁用,1-启用")
    private Integer isEnabled;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "步骤列表")
    private List<DisposalPlanStep> steps;
}

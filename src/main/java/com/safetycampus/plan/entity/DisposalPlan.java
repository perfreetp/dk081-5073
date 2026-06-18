package com.safetycampus.plan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.safetycampus.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("disposal_plan")
@Schema(description = "处置预案")
public class DisposalPlan extends BaseEntity {

    @Schema(description = "预案名称")
    private String planName;

    @Schema(description = "预案编码")
    private String planCode;

    @Schema(description = "适用警情级别:1-重大,2-较大,3-一般")
    private Integer alarmLevel;

    @Schema(description = "适用学校类型,逗号分隔:1-幼儿园,2-小学,3-初中,4-高中,5-中职,6-高校")
    private String schoolTypes;

    @Schema(description = "预案描述")
    private String description;

    @Schema(description = "是否启用:0-禁用,1-启用")
    private Integer isEnabled;

    @Schema(description = "排序")
    private Integer sortOrder;
}

package com.safetycampus.notifyrule.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.safetycampus.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("notify_rule_target")
@Schema(description = "通知规则目标")
public class NotifyRuleTarget extends BaseEntity {

    @Schema(description = "规则ID")
    private Long ruleId;

    @Schema(description = "目标类型:1-教育局值班,2-学校保卫,3-派出所,4-指定人员")
    private Integer targetType;

    @Schema(description = "目标ID")
    private Long targetId;

    @Schema(description = "目标名称")
    private String targetName;

    @Schema(description = "目标电话")
    private String targetPhone;

    @Schema(description = "排序")
    private Integer sortOrder;
}

package com.safetycampus.notifyrule.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.safetycampus.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("notify_rule")
@Schema(description = "通知规则")
public class NotifyRule extends BaseEntity {

    @Schema(description = "规则名称")
    private String ruleName;

    @Schema(description = "规则类型:1-默认规则,2-节假日规则,3-夜间规则,4-自定义规则")
    private Integer ruleType;

    @Schema(description = "优先级,数字越大优先级越高")
    private Integer priority;

    @Schema(description = "是否启用:0-禁用,1-启用")
    private Integer isEnabled;

    @Schema(description = "节假日模式:0-否,1-是")
    private Integer holidayMode;

    @Schema(description = "夜间模式:0-否,1-是")
    private Integer nightMode;

    @Schema(description = "适用学校类型,逗号分隔")
    private String schoolTypes;

    @Schema(description = "适用警情级别,逗号分隔")
    private String alarmLevels;

    @Schema(description = "通知渠道,逗号分隔")
    private String notifyChannels;

    @Schema(description = "通知目标配置(JSON)")
    private String notifyTargets;

    @Schema(description = "通知模板")
    private String notifyTemplate;

    @Schema(description = "规则描述")
    private String description;
}

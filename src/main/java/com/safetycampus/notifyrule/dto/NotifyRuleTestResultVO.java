package com.safetycampus.notifyrule.dto;

import com.safetycampus.notifyrule.entity.NotifyRule;
import com.safetycampus.notifyrule.entity.NotifyRuleTarget;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "规则试算结果VO")
public class NotifyRuleTestResultVO {

    @Schema(description = "匹配的规则列表")
    private List<NotifyRule> matchedRules;

    @Schema(description = "通知目标列表")
    private List<NotifyRuleTarget> targets;

    @Schema(description = "通知渠道列表")
    private List<Integer> notifyChannels;

    @Schema(description = "通知模板")
    private String notifyTemplate;
}

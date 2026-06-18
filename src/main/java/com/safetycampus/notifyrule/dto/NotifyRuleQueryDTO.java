package com.safetycampus.notifyrule.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safetycampus.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "通知规则查询DTO")
public class NotifyRuleQueryDTO extends PageQuery {

    @Schema(description = "规则名称")
    private String ruleName;

    @Schema(description = "规则类型:1-默认规则,2-节假日规则,3-夜间规则,4-自定义规则")
    private Integer ruleType;

    @Schema(description = "是否启用:0-禁用,1-启用")
    private Integer isEnabled;

    @Schema(description = "节假日模式:0-否,1-是")
    private Integer holidayMode;

    @Schema(description = "夜间模式:0-否,1-是")
    private Integer nightMode;

    @JsonIgnore
    public long getOffset() {
        return (long) (getPageNum() - 1) * getPageSize();
    }
}

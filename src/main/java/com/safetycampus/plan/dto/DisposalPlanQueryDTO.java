package com.safetycampus.plan.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safetycampus.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "处置预案查询DTO")
public class DisposalPlanQueryDTO extends PageQuery {

    @Schema(description = "预案名称")
    private String planName;

    @Schema(description = "预案编码")
    private String planCode;

    @Schema(description = "适用警情级别:1-重大,2-较大,3-一般")
    private Integer alarmLevel;

    @Schema(description = "是否启用:0-禁用,1-启用")
    private Integer isEnabled;

    @JsonIgnore
    public long getOffset() {
        return (long) (getPageNum() - 1) * getPageSize();
    }
}

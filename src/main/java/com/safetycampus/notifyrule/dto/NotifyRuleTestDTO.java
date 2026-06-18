package com.safetycampus.notifyrule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "规则试算DTO")
public class NotifyRuleTestDTO {

    @Schema(description = "学校ID")
    @NotNull(message = "学校ID不能为空")
    private Long schoolId;

    @Schema(description = "警情级别:1-重大,2-较大,3-一般")
    @NotNull(message = "警情级别不能为空")
    private Integer alarmLevel;

    @Schema(description = "警情类型")
    @NotNull(message = "警情类型不能为空")
    private Integer alarmType;

    @Schema(description = "是否节假日")
    private Boolean isHoliday;

    @Schema(description = "是否夜间")
    private Boolean isNight;
}

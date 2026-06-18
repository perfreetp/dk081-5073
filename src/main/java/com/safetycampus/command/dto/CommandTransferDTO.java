package com.safetycampus.command.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "警情转派参数")
public class CommandTransferDTO implements Serializable {

    @Schema(description = "警情ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "警情ID不能为空")
    private Long alarmId;

    @Schema(description = "目标学校ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "目标学校ID不能为空")
    private Long targetSchoolId;

    @Schema(description = "目标用户ID")
    private Long targetUserId;

    @Schema(description = "转派原因", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "转派原因不能为空")
    private String transferReason;
}

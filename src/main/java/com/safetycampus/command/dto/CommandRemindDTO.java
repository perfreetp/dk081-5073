package com.safetycampus.command.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "一键催办参数")
public class CommandRemindDTO implements Serializable {

    @Schema(description = "警情ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "警情ID不能为空")
    private Long alarmId;

    @Schema(description = "催办内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "催办内容不能为空")
    private String remindContent;
}

package com.safetycampus.supervise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "督办创建DTO")
public class SuperviseCreateDTO {

    @Schema(description = "警情ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "警情ID不能为空")
    private Long alarmId;

    @Schema(description = "督办要求", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "督办要求不能为空")
    private String superviseRequire;

    @Schema(description = "督办人ID")
    private Long supervisorId;

    @Schema(description = "督办人姓名")
    private String supervisorName;
}

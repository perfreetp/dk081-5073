package com.safetycampus.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "系统参数DTO")
public class SysParamDTO implements Serializable {

    @Schema(description = "ID")
    private Long id;

    @NotBlank(message = "参数键不能为空")
    @Schema(description = "参数键", requiredMode = Schema.RequiredMode.REQUIRED)
    private String paramKey;

    @Schema(description = "参数值")
    private String paramValue;

    @NotBlank(message = "参数名称不能为空")
    @Schema(description = "参数名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String paramName;

    @Schema(description = "描述")
    private String description;
}

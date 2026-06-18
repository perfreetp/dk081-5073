package com.safetycampus.supervise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "处置反馈DTO")
public class HandleFeedbackDTO {

    @Schema(description = "警情ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "警情ID不能为空")
    private Long alarmId;

    @Schema(description = "处置状态：1-处置中 4-已处置", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "处置状态不能为空")
    private Integer handleStatus;

    @Schema(description = "处置结果", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "处置结果不能为空")
    private String handleResult;

    @Schema(description = "处置附件URL")
    private String attachUrl;

    @Schema(description = "处置人ID")
    private Long handlerId;

    @Schema(description = "处置人姓名")
    private String handlerName;
}

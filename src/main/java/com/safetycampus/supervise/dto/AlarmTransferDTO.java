package com.safetycampus.supervise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "警情转派DTO")
public class AlarmTransferDTO {

    @Schema(description = "警情ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "警情ID不能为空")
    private Long alarmId;

    @Schema(description = "目标学校ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "目标学校ID不能为空")
    private Long targetSchoolId;

    @Schema(description = "目标学校名称")
    private String targetSchoolName;

    @Schema(description = "转派原因", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "转派原因不能为空")
    private String transferReason;

    @Schema(description = "转派人ID")
    private Long operatorId;

    @Schema(description = "转派人姓名")
    private String operatorName;
}

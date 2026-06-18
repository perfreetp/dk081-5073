package com.safetycampus.command.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "补充说明参数")
public class CommandNoteDTO implements Serializable {

    @Schema(description = "警情ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "警情ID不能为空")
    private Long alarmId;

    @Schema(description = "补充说明内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "补充说明内容不能为空")
    private String noteContent;

    @Schema(description = "附件URL")
    private String attachUrl;

    @Schema(description = "是否重要:0-否,1-是")
    private Integer isImportant;
}

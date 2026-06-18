package com.safetycampus.notify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class NotifySendDTO implements Serializable {

    private Long alarmId;

    @NotNull(message = "通知类型不能为空")
    private Integer notifyType;

    @NotEmpty(message = "通知目标不能为空")
    private List<String> targets;

    private List<String> targetNames;

    @NotBlank(message = "通知标题不能为空")
    private String title;

    @NotBlank(message = "通知内容不能为空")
    private String content;

    private String templateCode;
}

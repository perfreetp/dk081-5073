package com.safetycampus.command.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.safetycampus.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("alarm_supplement_note")
public class AlarmSupplementNote extends BaseEntity {

    private Long alarmId;

    private Long operatorId;

    private String operatorName;

    private String operatorRole;

    private String noteContent;

    private String attachUrl;

    private Integer isImportant;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}

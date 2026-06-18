package com.safetycampus.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_audit_log")
public class SysAuditLog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String username;

    private String module;

    private String operation;

    private String ip;

    private String userAgent;

    private String params;

    private String result;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}

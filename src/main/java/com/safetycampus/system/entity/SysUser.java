package com.safetycampus.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.safetycampus.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    private String username;

    @JsonIgnore
    private String password;

    private String realName;

    private String phone;

    private String email;

    private Long deptId;

    private Integer roleType;

    private Long schoolId;

    private Long policeStationId;

    private Integer status;

    private Integer dutyShift;
}

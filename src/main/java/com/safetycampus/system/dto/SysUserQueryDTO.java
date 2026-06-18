package com.safetycampus.system.dto;

import com.safetycampus.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户查询参数")
public class SysUserQueryDTO extends PageQuery implements Serializable {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "角色类型")
    private Integer roleType;

    @Schema(description = "学校ID")
    private Long schoolId;

    @Schema(description = "状态:0-禁用,1-启用")
    private Integer status;

    @Schema(description = "关键词(用户名/姓名/手机号)")
    private String keyword;
}

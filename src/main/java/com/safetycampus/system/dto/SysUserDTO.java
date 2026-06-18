package com.safetycampus.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "用户信息DTO")
public class SysUserDTO implements Serializable {

    @Schema(description = "用户ID")
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "密码")
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    @Schema(description = "真实姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String realName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "部门ID")
    private Long deptId;

    @NotNull(message = "角色类型不能为空")
    @Schema(description = "角色类型:1-教育局管理员,2-教育局值班,3-学校保卫,4-派出所联络员", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer roleType;

    @Schema(description = "关联学校ID(学校保卫)")
    private Long schoolId;

    @Schema(description = "关联派出所ID(联络员)")
    private Long policeStationId;

    @Schema(description = "状态:0-禁用,1-启用")
    private Integer status;
}

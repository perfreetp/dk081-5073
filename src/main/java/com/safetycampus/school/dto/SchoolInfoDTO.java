package com.safetycampus.school.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "学校新增/编辑DTO")
public class SchoolInfoDTO {

    @Schema(description = "学校ID")
    private Long id;

    @Schema(description = "学校编码")
    @NotBlank(message = "学校编码不能为空")
    private String schoolCode;

    @Schema(description = "学校名称")
    @NotBlank(message = "学校名称不能为空")
    private String schoolName;

    @Schema(description = "学校类型:1-幼儿园,2-小学,3-初中,4-高中,5-中职,6-高校")
    @NotNull(message = "学校类型不能为空")
    private Integer schoolType;

    @Schema(description = "风险等级:1-重点,2-关注,3-普通")
    private Integer schoolLevel;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "校长")
    private String principal;

    @Schema(description = "校长电话")
    private String principalPhone;

    @Schema(description = "保卫主任")
    private String securityLeader;

    @Schema(description = "保卫电话")
    private String securityPhone;

    @Schema(description = "属地派出所ID")
    private Long policeStationId;

    @Schema(description = "学校分组ID")
    private Long groupId;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "学生人数")
    private Integer studentCount;

    @Schema(description = "教职工人数")
    private Integer teacherCount;

    @Schema(description = "状态:0-停用,1-正常")
    private Integer status;
}

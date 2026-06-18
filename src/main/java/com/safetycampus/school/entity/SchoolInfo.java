package com.safetycampus.school.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.safetycampus.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("school_info")
@Schema(description = "学校信息")
public class SchoolInfo extends BaseEntity {

    @Schema(description = "学校编码")
    private String schoolCode;

    @Schema(description = "学校名称")
    private String schoolName;

    @Schema(description = "学校类型:1-幼儿园,2-小学,3-初中,4-高中,5-中职,6-高校")
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

    @Schema(description = "报警设备数量")
    private Integer deviceCount;

    @Schema(description = "状态:0-停用,1-正常")
    private Integer status;
}

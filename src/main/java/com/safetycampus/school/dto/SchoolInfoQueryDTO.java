package com.safetycampus.school.dto;

import com.safetycampus.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "学校查询DTO")
public class SchoolInfoQueryDTO extends PageQuery {

    @Schema(description = "学校编码")
    private String schoolCode;

    @Schema(description = "学校名称")
    private String schoolName;

    @Schema(description = "学校类型:1-幼儿园,2-小学,3-初中,4-高中,5-中职,6-高校")
    private Integer schoolType;

    @Schema(description = "风险等级:1-重点,2-关注,3-普通")
    private Integer schoolLevel;

    @Schema(description = "学校分组ID")
    private Long groupId;

    @Schema(description = "属地派出所ID")
    private Long policeStationId;

    @Schema(description = "状态:0-停用,1-正常")
    private Integer status;
}

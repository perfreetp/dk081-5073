package com.safetycampus.overview.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.safetycampus.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "总览查询参数")
public class OverviewQueryDTO extends PageQuery {

    @Schema(description = "学校ID")
    private Long schoolId;

    @Schema(description = "街镇ID")
    private Long townId;

    @Schema(description = "学校类型:1-幼儿园,2-小学,3-初中,4-高中,5-中职,6-高校")
    private Integer schoolType;

    @Schema(description = "警情类型:1-紧急求助,2-火灾,3-治安,4-校园欺凌,5-食物中毒,6-自然灾害,7-其他")
    private Integer alarmType;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}

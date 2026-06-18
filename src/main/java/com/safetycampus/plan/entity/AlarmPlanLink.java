package com.safetycampus.plan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.safetycampus.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("alarm_plan_link")
@Schema(description = "警情预案关联")
public class AlarmPlanLink extends BaseEntity {

    @Schema(description = "警情ID")
    private Long alarmId;

    @Schema(description = "预案ID")
    private Long planId;

    @Schema(description = "匹配时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime matchedTime;

    @Schema(description = "启动时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startedAt;

    @Schema(description = "完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;

    @Schema(description = "完成率(%)")
    private BigDecimal completionRate;

    @Schema(description = "状态:1-已匹配,2-已启动,3-执行中,4-已完成,5-已取消")
    private Integer status;

    @Schema(description = "总结")
    private String summary;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}

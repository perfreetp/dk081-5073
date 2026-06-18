package com.safetycampus.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "单个学校对比项")
public class SchoolCompareItemVO implements Serializable {

    @Schema(description = "学校ID")
    private Long schoolId;

    @Schema(description = "学校名称")
    private String schoolName;

    @Schema(description = "学校类型")
    private Integer schoolType;

    @Schema(description = "当前期警情数")
    private Long currentAlarmCount;

    @Schema(description = "对比期警情数")
    private Long compareAlarmCount;

    @Schema(description = "当前期重大警情数")
    private Long currentCriticalAlarmCount;

    @Schema(description = "对比期重大警情数")
    private Long compareCriticalAlarmCount;

    @Schema(description = "警情变化率")
    private BigDecimal alarmChangeRate;

    @Schema(description = "警情变化趋势: up-上升, down-下降, flat-持平")
    private String alarmChangeTrend;

    @Schema(description = "当前期平均响应时间")
    private BigDecimal currentAvgResponseTime;

    @Schema(description = "对比期平均响应时间")
    private BigDecimal compareAvgResponseTime;

    @Schema(description = "响应时间变化率")
    private BigDecimal responseChangeRate;

    @Schema(description = "响应时间变化趋势: up-上升, down-下降, flat-持平")
    private String responseChangeTrend;

    @Schema(description = "当前期处置完成率")
    private BigDecimal currentHandleRate;

    @Schema(description = "对比期处置完成率")
    private BigDecimal compareHandleRate;

    @Schema(description = "处置完成率变化率")
    private BigDecimal handleChangeRate;

    @Schema(description = "处置完成率变化趋势: up-上升, down-下降, flat-持平")
    private String handleChangeTrend;

    @Schema(description = "当前期超时次数")
    private Long currentTimeoutCount;

    @Schema(description = "对比期超时次数")
    private Long compareTimeoutCount;

    @Schema(description = "超时次数变化率")
    private BigDecimal timeoutChangeRate;

    @Schema(description = "超时次数变化趋势: up-上升, down-下降, flat-持平")
    private String timeoutChangeTrend;

    @Schema(description = "当前期排名")
    private Integer currentRank;

    @Schema(description = "对比期排名")
    private Integer compareRank;

    @Schema(description = "排名变化: 正数上升, 负数下降")
    private Integer rankChange;

    @Schema(description = "高风险原因摘要")
    private String riskSummary;
}

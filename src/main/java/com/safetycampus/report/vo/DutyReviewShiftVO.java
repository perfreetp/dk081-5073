package com.safetycampus.report.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "班次复盘数据")
public class DutyReviewShiftVO implements Serializable {

    @Schema(description = "值班日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dutyDate;

    @Schema(description = "值班班次ID")
    private Long dutyShiftId;

    @Schema(description = "班次名称:白班/夜班")
    private String dutyShiftName;

    @Schema(description = "值班人员ID")
    private Long dutyUserId;

    @Schema(description = "值班人员姓名")
    private String dutyUserName;

    @Schema(description = "总接警量")
    private Long totalAlarmCount;

    @Schema(description = "重大警情数")
    private Long criticalAlarmCount;

    @Schema(description = "响应超时数")
    private Long timeoutResponseCount;

    @Schema(description = "响应超时率(%)")
    private BigDecimal responseTimeoutRate;

    @Schema(description = "平均响应时间(秒)")
    private BigDecimal avgResponseTime;

    @Schema(description = "催办次数")
    private Long remindCount;

    @Schema(description = "派出所应反馈数")
    private Long policeFeedbackCount;

    @Schema(description = "派出所及时反馈数")
    private Long policeFeedbackInTimeCount;

    @Schema(description = "派出所反馈及时率(%)")
    private BigDecimal policeFeedbackRate;

    @Schema(description = "派出所平均反馈时长(秒)")
    private BigDecimal policeFeedbackAvgTime;

    @Schema(description = "平均处置时长(秒)")
    private BigDecimal avgHandleTime;

    @Schema(description = "处置完成率(%)")
    private BigDecimal completionRate;

    @Schema(description = "已关闭警情数")
    private Long closeCount;

    @Schema(description = "遗留未处置警情数")
    private Long carryCount;

    @Schema(description = "班次评分(百分制)")
    private BigDecimal rankScore;

    @Schema(description = "本班次重大事件列表")
    private List<Map<String, Object>> riskEvents;
}

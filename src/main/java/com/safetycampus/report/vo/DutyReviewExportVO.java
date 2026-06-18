package com.safetycampus.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "值守复盘导出VO")
public class DutyReviewExportVO implements Serializable {

    @Schema(description = "导出日期")
    private LocalDate dutyDate;

    @Schema(description = "班次名称")
    private String dutyShiftName;

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

    @Schema(description = "派出所反馈及时率(%)")
    private BigDecimal policeFeedbackRate;

    @Schema(description = "处置完成率(%)")
    private BigDecimal completionRate;

    @Schema(description = "班次评分")
    private BigDecimal rankScore;
}

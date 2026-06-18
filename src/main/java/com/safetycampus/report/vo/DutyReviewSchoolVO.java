package com.safetycampus.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "学校维度复盘数据")
public class DutyReviewSchoolVO implements Serializable {

    @Schema(description = "学校ID")
    private Long schoolId;

    @Schema(description = "学校名称")
    private String schoolName;

    @Schema(description = "学校类型名称")
    private String schoolTypeName;

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

    @Schema(description = "平均处置时长(秒)")
    private BigDecimal avgHandleTime;

    @Schema(description = "催办次数")
    private Long remindCount;

    @Schema(description = "派出所反馈及时率(%)")
    private BigDecimal policeFeedbackRate;

    @Schema(description = "处置完成率(%)")
    private BigDecimal completionRate;

    @Schema(description = "风险摘要")
    private String riskSummary;
}

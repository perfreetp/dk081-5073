package com.safetycampus.overview.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "大屏总览返回VO")
public class OverviewDashboardVO implements Serializable {

    @Schema(description = "今日警情数")
    private Long todayAlarmCount;

    @Schema(description = "今日重大警情数")
    private Long todayCriticalCount;

    @Schema(description = "今日超时警情数")
    private Long todayTimeoutCount;

    @Schema(description = "处置中警情数")
    private Long processingCount;

    @Schema(description = "已处置警情数")
    private Long handledCount;

    @Schema(description = "已关闭警情数")
    private Long closedCount;

    @Schema(description = "平均响应时间(秒)")
    private BigDecimal avgResponseTime;

    @Schema(description = "平均处置时间(秒)")
    private BigDecimal avgHandleTime;

    @Schema(description = "学校类型分布")
    private List<SchoolTypeDistribution> schoolTypeDistribution;

    @Schema(description = "街镇分布")
    private List<TownDistribution> townDistribution;

    @Schema(description = "警情类型分布")
    private List<AlarmTypeDistribution> alarmTypeDistribution;

    @Schema(description = "派出所联动进展")
    private List<PoliceStationProgress> policeStationProgress;

    @Schema(description = "最近10条警情列表")
    private List<RecentAlarm> recentAlarms;

    @Schema(description = "超时未响应列表")
    private List<TimeoutAlarm> timeoutAlarmList;

    @Data
    @Schema(description = "学校类型分布")
    public static class SchoolTypeDistribution implements Serializable {
        @Schema(description = "类型编码")
        private Integer type;
        @Schema(description = "类型名称")
        private String name;
        @Schema(description = "数量")
        private Long count;
    }

    @Data
    @Schema(description = "街镇分布")
    public static class TownDistribution implements Serializable {
        @Schema(description = "街镇ID")
        private Long townId;
        @Schema(description = "街镇名称")
        private String townName;
        @Schema(description = "数量")
        private Long count;
    }

    @Data
    @Schema(description = "警情类型分布")
    public static class AlarmTypeDistribution implements Serializable {
        @Schema(description = "类型编码")
        private Integer type;
        @Schema(description = "类型名称")
        private String name;
        @Schema(description = "数量")
        private Long count;
    }

    @Data
    @Schema(description = "派出所联动进展")
    public static class PoliceStationProgress implements Serializable {
        @Schema(description = "派出所ID")
        private Long stationId;
        @Schema(description = "派出所名称")
        private String stationName;
        @Schema(description = "总警情数")
        private Long totalCount;
        @Schema(description = "已处置数")
        private Long handledCount;
        @Schema(description = "超时数")
        private Long timeoutCount;
    }

    @Data
    @Schema(description = "近期警情")
    public static class RecentAlarm implements Serializable {
        @Schema(description = "警情ID")
        private Long id;
        @Schema(description = "警情编号")
        private String alarmNo;
        @Schema(description = "学校名称")
        private String schoolName;
        @Schema(description = "警情类型")
        private Integer alarmType;
        @Schema(description = "警情类型名称")
        private String alarmTypeName;
        @Schema(description = "警情级别")
        private Integer alarmLevel;
        @Schema(description = "警情级别名称")
        private String alarmLevelName;
        @Schema(description = "警情标题")
        private String alarmTitle;
        @Schema(description = "状态")
        private Integer status;
        @Schema(description = "状态名称")
        private String statusName;
        @Schema(description = "报警时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
    }

    @Data
    @Schema(description = "超时警情")
    public static class TimeoutAlarm implements Serializable {
        @Schema(description = "警情ID")
        private Long id;
        @Schema(description = "警情编号")
        private String alarmNo;
        @Schema(description = "学校名称")
        private String schoolName;
        @Schema(description = "警情类型")
        private Integer alarmType;
        @Schema(description = "警情类型名称")
        private String alarmTypeName;
        @Schema(description = "警情级别")
        private Integer alarmLevel;
        @Schema(description = "警情级别名称")
        private String alarmLevelName;
        @Schema(description = "警情标题")
        private String alarmTitle;
        @Schema(description = "报警时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
        @Schema(description = "超时时长(分钟)")
        private Long timeoutMinutes;
    }
}

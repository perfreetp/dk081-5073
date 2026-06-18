package com.safetycampus.command.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "值守指挥研判台主视图")
public class CommandDashboardVO implements Serializable {

    @Schema(description = "警情基本信息")
    private AlarmBriefVO alarmBrief;

    @Schema(description = "学校信息")
    private SchoolDetailVO schoolDetail;

    @Schema(description = "附近派出所信息")
    private PoliceStationVO nearbyPoliceStation;

    @Schema(description = "最近10条历史报警记录")
    private List<AlarmHistoryVO> alarmHistoryList;

    @Schema(description = "风险画像")
    private RiskPortraitVO riskPortrait;

    @Schema(description = "当前通知链路")
    private NotifyLinkVO notifyLink;

    @Schema(description = "督办进度")
    private SuperviseProgressVO superviseProgress;

    @Schema(description = "补充说明列表")
    private List<SupplementNoteVO> supplementNotes;

    @Data
    @Schema(description = "警情基本信息")
    public static class AlarmBriefVO implements Serializable {
        @Schema(description = "警情ID")
        private Long id;

        @Schema(description = "警情编号")
        private String alarmNo;

        @Schema(description = "报警类型")
        private Integer alarmType;

        @Schema(description = "报警类型名称")
        private String alarmTypeName;

        @Schema(description = "警情级别")
        private Integer alarmLevel;

        @Schema(description = "警情级别名称")
        private String alarmLevelName;

        @Schema(description = "警情标题")
        private String alarmTitle;

        @Schema(description = "位置")
        private String location;

        @Schema(description = "经度")
        private BigDecimal longitude;

        @Schema(description = "纬度")
        private BigDecimal latitude;

        @Schema(description = "创建时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        @Schema(description = "状态")
        private Integer status;

        @Schema(description = "状态名称")
        private String statusName;
    }

    @Data
    @Schema(description = "学校详细信息")
    public static class SchoolDetailVO implements Serializable {
        @Schema(description = "学校ID")
        private Long id;

        @Schema(description = "学校名称")
        private String schoolName;

        @Schema(description = "学校类型")
        private Integer schoolType;

        @Schema(description = "学校类型名称")
        private String schoolTypeName;

        @Schema(description = "学校等级")
        private Integer schoolLevel;

        @Schema(description = "学校等级名称")
        private String schoolLevelName;

        @Schema(description = "地址")
        private String address;

        @Schema(description = "经度")
        private BigDecimal longitude;

        @Schema(description = "纬度")
        private BigDecimal latitude;

        @Schema(description = "校长")
        private String principal;

        @Schema(description = "保卫主任")
        private String securityLeader;

        @Schema(description = "联系电话")
        private String contactPhone;

        @Schema(description = "学生人数")
        private Integer studentCount;

        @Schema(description = "设备数")
        private Integer deviceCount;
    }

    @Data
    @Schema(description = "附近派出所信息")
    public static class PoliceStationVO implements Serializable {
        @Schema(description = "派出所ID")
        private Long id;

        @Schema(description = "派出所名称")
        private String stationName;

        @Schema(description = "联络人")
        private String liaison;

        @Schema(description = "联络电话")
        private String liaisonPhone;

        @Schema(description = "值班电话")
        private String dutyPhone;

        @Schema(description = "地址")
        private String address;

        @Schema(description = "距离(km)")
        private BigDecimal distanceKm;
    }

    @Data
    @Schema(description = "历史报警记录")
    public static class AlarmHistoryVO implements Serializable {
        @Schema(description = "警情ID")
        private Long id;

        @Schema(description = "警情编号")
        private String alarmNo;

        @Schema(description = "报警类型")
        private Integer alarmType;

        @Schema(description = "报警类型名称")
        private String alarmTypeName;

        @Schema(description = "警情级别")
        private Integer alarmLevel;

        @Schema(description = "警情级别名称")
        private String alarmLevelName;

        @Schema(description = "状态")
        private Integer status;

        @Schema(description = "状态名称")
        private String statusName;

        @Schema(description = "创建时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;

        @Schema(description = "处置耗时(分钟)")
        private Long handleDurationMinutes;
    }

    @Data
    @Schema(description = "风险画像")
    public static class RiskPortraitVO implements Serializable {
        @Schema(description = "统计月份")
        private String statMonth;

        @Schema(description = "本月报警数")
        private Integer totalAlarms;

        @Schema(description = "重大警情数")
        private Integer criticalAlarms;

        @Schema(description = "平均响应时间(秒)")
        private Integer avgResponseTime;

        @Schema(description = "风险评分")
        private BigDecimal riskScore;

        @Schema(description = "风险等级")
        private Integer riskLevel;

        @Schema(description = "风险等级名称")
        private String riskLevelName;

        @Schema(description = "主要风险点")
        private List<String> mainRiskPoints;
    }

    @Data
    @Schema(description = "通知链路")
    public static class NotifyLinkVO implements Serializable {
        @Schema(description = "已发送数量")
        private Integer sentCount;

        @Schema(description = "目标总数")
        private Integer targetTotal;

        @Schema(description = "各渠道状态")
        private List<ChannelStatusVO> channelStatusList;

        @Schema(description = "已读列表")
        private List<NotifyUserVO> readList;

        @Schema(description = "未读列表")
        private List<NotifyUserVO> unreadList;

        @Schema(description = "失败列表")
        private List<NotifyUserVO> failedList;
    }

    @Data
    @Schema(description = "渠道状态")
    public static class ChannelStatusVO implements Serializable {
        @Schema(description = "渠道类型")
        private Integer channelType;

        @Schema(description = "渠道名称")
        private String channelName;

        @Schema(description = "总数")
        private Integer total;

        @Schema(description = "已发送")
        private Integer sent;

        @Schema(description = "失败")
        private Integer failed;
    }

    @Data
    @Schema(description = "通知用户信息")
    public static class NotifyUserVO implements Serializable {
        @Schema(description = "通知记录ID")
        private Long id;

        @Schema(description = "目标姓名")
        private String targetName;

        @Schema(description = "通知目标(手机号/设备ID)")
        private String notifyTarget;

        @Schema(description = "通知渠道类型")
        private Integer notifyType;

        @Schema(description = "通知渠道名称")
        private String notifyTypeName;

        @Schema(description = "发送时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime sentAt;

        @Schema(description = "阅读时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime readAt;

        @Schema(description = "失败原因")
        private String failReason;
    }

    @Data
    @Schema(description = "督办进度")
    public static class SuperviseProgressVO implements Serializable {
        @Schema(description = "当前状态")
        private Integer currentStatus;

        @Schema(description = "当前状态名称")
        private String currentStatusName;

        @Schema(description = "督办人ID")
        private Long supervisorId;

        @Schema(description = "督办人姓名")
        private String supervisorName;

        @Schema(description = "督办时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime superviseTime;

        @Schema(description = "处置人ID")
        private Long handlerId;

        @Schema(description = "处置人姓名")
        private String handlerName;

        @Schema(description = "反馈时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime feedbackTime;

        @Schema(description = "流转步骤列表")
        private List<FlowStepVO> flowSteps;
    }

    @Data
    @Schema(description = "流转步骤")
    public static class FlowStepVO implements Serializable {
        @Schema(description = "流转记录ID")
        private Long id;

        @Schema(description = "流转类型")
        private Integer flowType;

        @Schema(description = "流转类型名称")
        private String flowTypeName;

        @Schema(description = "操作人ID")
        private Long operatorId;

        @Schema(description = "操作人姓名")
        private String operatorName;

        @Schema(description = "操作人角色")
        private String operatorRole;

        @Schema(description = "参与方类型")
        private Integer partyType;

        @Schema(description = "参与方名称")
        private String partyName;

        @Schema(description = "耗时(秒)")
        private Integer durationSeconds;

        @Schema(description = "处理意见")
        private String remark;

        @Schema(description = "附件URL")
        private String attachUrl;

        @Schema(description = "创建时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
    }

    @Data
    @Schema(description = "补充说明")
    public static class SupplementNoteVO implements Serializable {
        @Schema(description = "补充说明ID")
        private Long id;

        @Schema(description = "警情ID")
        private Long alarmId;

        @Schema(description = "操作人ID")
        private Long operatorId;

        @Schema(description = "操作人姓名")
        private String operatorName;

        @Schema(description = "操作人角色")
        private String operatorRole;

        @Schema(description = "补充说明内容")
        private String noteContent;

        @Schema(description = "附件URL")
        private String attachUrl;

        @Schema(description = "是否重要:0-否,1-是")
        private Integer isImportant;

        @Schema(description = "创建时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
    }
}

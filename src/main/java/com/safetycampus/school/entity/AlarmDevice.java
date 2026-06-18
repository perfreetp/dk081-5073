package com.safetycampus.school.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.safetycampus.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("alarm_device")
@Schema(description = "报警设备")
public class AlarmDevice extends BaseEntity {

    @Schema(description = "设备编码")
    private String deviceCode;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "设备类型:1-一键报警柱,2-桌面报警盒,3-APP客户端")
    private Integer deviceType;

    @Schema(description = "学校ID")
    private Long schoolId;

    @Schema(description = "安装位置")
    private String location;

    @Schema(description = "状态:0-离线,1-在线,2-故障")
    private Integer status;

    @Schema(description = "最后在线时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastOnlineAt;
}

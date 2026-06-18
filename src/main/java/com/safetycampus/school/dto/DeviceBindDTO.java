package com.safetycampus.school.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "设备绑定DTO")
public class DeviceBindDTO {

    @Schema(description = "设备ID")
    private Long id;

    @Schema(description = "设备编码")
    @NotBlank(message = "设备编码不能为空")
    private String deviceCode;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "设备类型:1-一键报警柱,2-桌面报警盒,3-APP客户端")
    @NotNull(message = "设备类型不能为空")
    private Integer deviceType;

    @Schema(description = "学校ID")
    @NotNull(message = "学校ID不能为空")
    private Long schoolId;

    @Schema(description = "安装位置")
    private String location;

    @Schema(description = "状态:0-离线,1-在线,2-故障")
    private Integer status;
}

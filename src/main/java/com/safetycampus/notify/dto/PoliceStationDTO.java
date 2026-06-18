package com.safetycampus.notify.dto;

import com.safetycampus.common.entity.PageQuery;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class PoliceStationDTO extends PageQuery implements Serializable {

    private Long id;

    @NotBlank(message = "派出所编码不能为空")
    private String stationCode;

    @NotBlank(message = "派出所名称不能为空")
    private String stationName;

    private String liaison;

    private String liaisonPhone;

    private String dutyPhone;

    private String address;

    private Integer sortOrder;

    private Integer status;

    private String keyword;
}

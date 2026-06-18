package com.safetycampus.notify.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.safetycampus.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("police_station")
public class PoliceStation extends BaseEntity {

    private String stationCode;

    private String stationName;

    private String liaison;

    private String liaisonPhone;

    private String dutyPhone;

    private String address;

    private java.math.BigDecimal longitude;

    private java.math.BigDecimal latitude;

    private Integer sortOrder;

    private Integer status;

    private Integer feedbackDeadlineMinutes;
}

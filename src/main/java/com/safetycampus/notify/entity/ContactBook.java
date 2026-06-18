package com.safetycampus.notify.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.safetycampus.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("contact_book")
public class ContactBook extends BaseEntity {

    private String name;

    private String phone;

    private Integer unitType;

    private String unitName;

    private String position;

    private Long groupId;

    private Integer isDuty;

    private Integer sortOrder;
}

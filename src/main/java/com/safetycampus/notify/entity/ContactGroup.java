package com.safetycampus.notify.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.safetycampus.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("contact_group")
public class ContactGroup extends BaseEntity {

    private String groupName;

    private String description;

    private Integer sortOrder;
}

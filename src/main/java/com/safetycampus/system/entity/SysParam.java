package com.safetycampus.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.safetycampus.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_param")
public class SysParam extends BaseEntity {

    private String paramKey;

    private String paramValue;

    private String paramName;

    private String description;
}

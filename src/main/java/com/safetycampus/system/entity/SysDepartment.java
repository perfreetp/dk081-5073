package com.safetycampus.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.safetycampus.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_department")
public class SysDepartment extends BaseEntity {

    private String deptName;

    private Long parentId;

    private Integer deptType;

    private Integer sortOrder;
}

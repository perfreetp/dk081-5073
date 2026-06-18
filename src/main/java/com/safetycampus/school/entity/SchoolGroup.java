package com.safetycampus.school.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.safetycampus.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("school_group")
@Schema(description = "学校分组")
public class SchoolGroup extends BaseEntity {

    @Schema(description = "分组名称")
    private String groupName;

    @Schema(description = "分组类型:1-按类型,2-按片区,3-自定义")
    private Integer groupType;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "排序")
    private Integer sortOrder;
}

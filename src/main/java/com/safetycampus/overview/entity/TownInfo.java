package com.safetycampus.overview.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.safetycampus.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("town_info")
@Schema(description = "街镇信息")
public class TownInfo extends BaseEntity {

    @Schema(description = "街镇编码")
    private String townCode;

    @Schema(description = "街镇名称")
    private String townName;

    @Schema(description = "排序")
    private Integer sortOrder;
}

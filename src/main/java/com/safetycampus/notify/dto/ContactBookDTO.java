package com.safetycampus.notify.dto;

import com.safetycampus.common.entity.PageQuery;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class ContactBookDTO extends PageQuery implements Serializable {

    private Long id;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotNull(message = "单位类型不能为空")
    private Integer unitType;

    private String unitName;

    private String position;

    private Long groupId;

    private Integer isDuty;

    private Integer sortOrder;

    private String keyword;
}

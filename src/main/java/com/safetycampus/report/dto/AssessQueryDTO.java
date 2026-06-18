package com.safetycampus.report.dto;

import com.safetycampus.common.entity.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class AssessQueryDTO extends PageQuery implements Serializable {

    private Long schoolId;

    private Long groupId;

    private String statQuarter;

    private String grade;

    private String keyword;
}

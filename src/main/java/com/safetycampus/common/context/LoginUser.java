package com.safetycampus.common.context;

import com.safetycampus.common.enums.RoleTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser implements Serializable {

    private Long userId;

    private String username;

    private RoleTypeEnum roleType;

    private Long schoolId;

    private String schoolName;

    private String realName;

    private String phone;
}

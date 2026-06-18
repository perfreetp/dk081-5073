package com.safetycampus.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录返回结果")
public class LoginVO implements Serializable {

    @Schema(description = "访问令牌")
    private String token;

    @Schema(description = "令牌前缀")
    private String tokenType;

    @Schema(description = "过期时间(秒)")
    private Long expiresIn;
}

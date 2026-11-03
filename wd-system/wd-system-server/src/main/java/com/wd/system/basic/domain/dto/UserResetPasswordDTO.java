package com.wd.system.basic.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * @author huangwenda
 * @date 2024/1/9 13:48
 **/
@Getter
@Setter
@Schema(description = "用户重置密码DTO")
public class UserResetPasswordDTO {

    @Schema(description = "用户ID")
    @NotNull(message = "ID不能为空")
    private Long id;

    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "确认密码")
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}

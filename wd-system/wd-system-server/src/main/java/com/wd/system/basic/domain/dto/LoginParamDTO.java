package com.wd.system.basic.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * @author huangwenda
 * @date 2024/1/4 15:55
 **/
@Getter
@Setter
@Schema(description = "登录参数")
public class LoginParamDTO {

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "zhang")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;
}

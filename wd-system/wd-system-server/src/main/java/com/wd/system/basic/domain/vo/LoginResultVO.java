package com.wd.system.basic.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * @author huangwenda
 * @date 2023/1/30 11:38
 **/
@Getter
@Setter
@Schema(description = "登录结果VO")
public class LoginResultVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "Token")
    private String token;
}

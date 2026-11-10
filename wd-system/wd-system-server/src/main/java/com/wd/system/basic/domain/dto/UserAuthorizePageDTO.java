package com.wd.system.basic.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户授权分页DTO。
 */
@Getter
@Setter
@Schema(description = "用户授权分页DTO")
public class UserAuthorizePageDTO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "状态(ENABLED:启用,DISABLE:禁用)")
    private String status;
}

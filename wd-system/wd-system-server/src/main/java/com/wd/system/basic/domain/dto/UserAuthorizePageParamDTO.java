package com.wd.system.basic.domain.dto;

import com.wd.common.core.annotions.EnumCodeExist;
import com.wd.common.core.model.BaseQuery;
import com.wd.system.basic.domain.enums.UserStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户授权分页查询参数。
 */
@Getter
@Setter
@Schema(description = "用户授权分页查询参数")
public class UserAuthorizePageParamDTO extends BaseQuery {

    @Schema(description = "角色ID")
    @NotNull(message = "角色ID不能为空")
    @Positive(message = "角色ID必须为正数")
    private Long roleId;

    @Schema(description = "是否已授权，true查询已授权用户，false查询未授权用户")
    @NotNull(message = "是否授权不能为空")
    private Boolean authorized;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "状态(ENABLED:启用,DISABLE:禁用)")
    @EnumCodeExist(enumClass = UserStatusEnum.class, message = "状态不正确")
    private String status;
}

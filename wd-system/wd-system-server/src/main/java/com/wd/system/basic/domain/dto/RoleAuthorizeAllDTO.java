package com.wd.system.basic.domain.dto;

import com.wd.common.core.annotions.EnumCodeExist;
import com.wd.system.basic.domain.enums.RolePermissionFlagEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "角色全部权限授权参数")
public class RoleAuthorizeAllDTO {

    @Schema(description = "角色ID")
    @NotNull(message = "角色ID不能为空")
    @Positive(message = "角色ID必须为正数")
    private Long roleId;

    @Schema(description = "是否授权，true授权，false取消授权")
    @NotNull(message = "授权操作不能为空")
    private Boolean authorized;

    @Schema(description = "权限标记")
    @EnumCodeExist(message = "权限标记不正确", enumClass = RolePermissionFlagEnum.class, required = true)
    private String permissionFlag;
}

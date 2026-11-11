package com.wd.system.basic.domain.dto;

import com.wd.common.core.annotions.EnumCodeExist;
import com.wd.system.basic.domain.enums.RoleAuthorizeOperationEnum;
import com.wd.system.basic.domain.enums.RolePermissionTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "角色指定类型的具体权限授权参数")
public class RoleAuthorizeDTO {

    @Schema(description = "角色ID")
    @NotNull(message = "角色ID不能为空")
    @Positive(message = "角色ID必须为正数")
    private Long roleId;

    @Schema(description = "授权类型(MENU:菜单权限,BUTTON:按钮权限,FIELD:字段权限,DATA_USER:用户数据权限)")
    @EnumCodeExist(message = "授权类型不正确", enumClass = RolePermissionTypeEnum.class, required = true)
    private String type;

    @Schema(description = "MENU 使用完整菜单 ID 集合，空集合清空；DATA_USER 使用本次选中的非空用户 ID 集合")
    private List<@NotNull(message = "关联ID不能为空") @Positive(message = "关联ID必须为正数") Long> relevanceIds;

    @Schema(description = "MENU 和 BUTTON 使用按钮编码，FIELD 使用字段编码；空集合清空对应授权")
    private List<@NotBlank(message = "关联值不能为空") String> relevanceValues;

    @Schema(description = "DATA_USER 专用且必填，GRANT 增加授权，REVOKE 取消授权")
    private RoleAuthorizeOperationEnum operation;
}

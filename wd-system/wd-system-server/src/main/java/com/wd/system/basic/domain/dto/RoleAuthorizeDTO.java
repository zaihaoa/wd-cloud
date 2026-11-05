package com.wd.system.basic.domain.dto;

import com.wd.common.core.annotions.EnumCodeExist;
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
@Schema(description = "角色具体权限授权参数，覆盖保存当前类型")
public class RoleAuthorizeDTO {

    @Schema(description = "角色ID")
    @NotNull(message = "角色ID不能为空")
    @Positive(message = "角色ID必须为正数")
    private Long roleId;

    @Schema(description = "授权类型(MENU:菜单权限,FIELD:字段权限,DATA_USER:用户数据权限)")
    @EnumCodeExist(message = "授权类型不正确", enumClass = RolePermissionTypeEnum.class, required = true)
    private String type;

    @Schema(description = "关联ID集合，MENU和DATA_USER使用；空集合清空当前类型授权")
    private List<@NotNull(message = "关联ID不能为空") @Positive(message = "关联ID必须为正数") Long> relevanceIds;

    @Schema(description = "关联值集合，FIELD使用字段编码；空集合清空当前类型授权")
    private List<@NotBlank(message = "关联值不能为空") String> relevanceValues;
}

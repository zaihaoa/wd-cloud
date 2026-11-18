package com.wd.system.basic.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "角色菜单和按钮授权参数")
public class RoleAuthorizeMenuDTO {

    @Schema(description = "角色ID")
    @NotNull(message = "角色ID不能为空")
    @Positive(message = "角色ID必须为正数")
    private Long roleId;

    @Schema(description = "完整的菜单ID集合，空集合清空")
    @NotNull(message = "必须提交菜单ID集合")
    private List<@NotNull(message = "菜单ID不能为空") @Positive(message = "菜单ID必须为正数") Long> menuIds;

    @Schema(description = "完整的按钮编码集合，空集合清空")
    @NotNull(message = "必须提交按钮编码集合")
    private List<@NotBlank(message = "按钮编码不能为空") String> buttonCodes;
}

package com.wd.system.basic.domain.dto;

import com.wd.common.core.annotions.EnumCodeExist;
import com.wd.common.core.annotions.ObjectCompare;
import com.wd.system.basic.domain.enums.MenuTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 菜单 分页查询参数
 * </p>
 *
 * @author huangwenda
 * @since 2024-01-08
 */
@Getter
@Setter
@Schema(description = "菜单更新参数")
public class MenuUpdateDTO {

    @Schema(description = "ID")
    @NotNull(message = "ID不能为空")
    private Long id;

    @Schema(description = "菜单名称")
    @ObjectCompare("菜单名称")
    @NotBlank(message = "菜单名称不能为空")
    @Size(min = 1, max = 100, message = "菜单名称长度不能超过100")
    private String name;

    @Schema(description = "父级ID")
    @ObjectCompare("父级ID")
    private Long parentId;

    @Schema(description = "显示顺序")
    @ObjectCompare("显示顺序")
    private Integer sortNumber;

    @Schema(description = "类型(DIRECTORY:目录,MENU:菜单 BUTTON:按钮)")
    @ObjectCompare("类型")
    @EnumCodeExist(message = "类型不正确", enumClass = MenuTypeEnum.class, required = true)
    private String menuType;

    @Schema(description = "路由地址")
    @ObjectCompare("路由地址")
    @Size(max = 200, message = "路由地址长度不能超过200")
    private String path;

    @Schema(description = "权限标志")
    @ObjectCompare("权限标志")
    @Size(max = 200, message = "权限标志长度不能超过200")
    private String permission;

    @Schema(description = "菜单图标")
    @ObjectCompare("菜单图标")
    @Size(max = 100, message = "菜单图标长度不能超过100")
    private String icon;

}

package com.wd.system.basic.domain.dto;

import com.wd.common.core.annotions.EnumCodeExist;
import com.wd.common.core.objectcompare.ObjectCompare;
import com.wd.system.basic.domain.enums.MenuStatusEnum;
import com.wd.system.basic.domain.enums.MenuTypeEnum;
import com.wd.system.basic.domain.model.MenuButton;
import com.wd.system.basic.domain.model.MenuRouteParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * <p>
 * 菜单更新参数
 * </p>
 *
 * @author huangwenda
 * @since 2024-01-08
 */
@Getter
@Setter
@Schema(description = "菜单更新参数")
public class MenuUpdateDTO {

    @Schema(description = "主键ID")
    @NotNull(message = "ID不能为空")
    private Long id;

    @Schema(description = "类型(DIRECTORY:目录,MENU:菜单)")
    @ObjectCompare("类型")
    @EnumCodeExist(message = "类型不正确", enumClass = MenuTypeEnum.class, required = true)
    @Size(max = 100, message = "类型长度不能超过100")
    private String menuType;

    @Schema(description = "菜单名称")
    @ObjectCompare("菜单名称")
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 100, message = "菜单名称长度不能超过100")
    private String menuName;

    @Schema(description = "状态(ENABLED:启用,DISABLED:禁用)")
    @ObjectCompare("状态")
    @EnumCodeExist(message = "状态不正确", enumClass = MenuStatusEnum.class)
    @Size(max = 100, message = "状态长度不能超过100")
    private String status;

    @Schema(description = "父级ID")
    @ObjectCompare("父级ID")
    private Long parentId;

    @Schema(description = "路由名称")
    @ObjectCompare("路由名称")
    @Size(max = 200, message = "路由名称长度不能超过200")
    private String routeName;

    @Schema(description = "路由路径")
    @ObjectCompare("路由路径")
    @Size(max = 200, message = "路由路径长度不能超过200")
    private String routePath;

    @Schema(description = "路由路径参数")
    @ObjectCompare("路由路径参数")
    @Size(max = 200, message = "路由路径参数长度不能超过200")
    private String routePathParam;

    @Schema(description = "权限标志")
    @ObjectCompare("权限标志")
    @Size(max = 200, message = "权限标志长度不能超过200")
    private String permission;

    @Schema(description = "布局")
    @ObjectCompare("布局")
    @Size(max = 200, message = "布局长度不能超过200")
    private String layout;

    @Schema(description = "组件")
    @ObjectCompare("组件")
    @Size(max = 200, message = "组件长度不能超过200")
    private String component;

    @Schema(description = "国际化KEY")
    @ObjectCompare("国际化KEY")
    @Size(max = 200, message = "国际化KEY长度不能超过200")
    private String i18nKey;

    @Schema(description = "显示顺序，越小越优先")
    @ObjectCompare("显示顺序，越小越优先")
    private Integer sortNumber;

    @Schema(description = "图标类型")
    @ObjectCompare("图标类型")
    @Size(max = 100, message = "图标类型长度不能超过100")
    private String iconType;

    @Schema(description = "图标")
    @ObjectCompare("图标")
    @Size(max = 100, message = "图标长度不能超过100")
    private String icon;

    @Schema(description = "缓存路由")
    @ObjectCompare("缓存路由")
    private Boolean cacheRoute;

    @Schema(description = "常量路由")
    @ObjectCompare("常量路由")
    private Boolean constantRoute;

    @Schema(description = "隐藏路由")
    @ObjectCompare("隐藏路由")
    private Boolean hideRoute;

    @Schema(description = "高亮路由名称")
    @ObjectCompare("高亮路由名称")
    @Size(max = 200, message = "高亮路由名称长度不能超过200")
    private String highlightRouteName;

    @Schema(description = "外链")
    @ObjectCompare("外链")
    @Size(max = 100, message = "外链长度不能超过100")
    private String externalLink;

    @Schema(description = "路由参数")
    @ObjectCompare("路由参数")
    private List<MenuRouteParam> routeParam;

    @Schema(description = "按钮")
    @ObjectCompare("按钮")
    private List<MenuButton> button;
}

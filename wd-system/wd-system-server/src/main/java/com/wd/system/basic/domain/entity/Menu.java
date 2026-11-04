package com.wd.system.basic.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wd.common.core.model.BaseUserTimeEntity;
import com.wd.system.basic.domain.model.MenuButton;
import com.wd.system.basic.domain.model.MenuRouteParam;
import com.wd.system.basic.handler.MenuButtonListHandler;
import com.wd.system.basic.handler.MenuRouteParamListHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 菜单
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@Getter
@Setter
@TableName(value = "sys_menu", autoResultMap = true)
@Schema(description = "菜单")
public class Menu extends BaseUserTimeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "类型(DIRECTORY:目录,MENU:菜单)")
    @TableField(value = "menu_type")
    private String menuType;

    @Schema(description = "菜单名称")
    @TableField(value = "menu_name")
    private String menuName;

    @Schema(description = "状态(ENABLED:启用,DISABLED:禁用)")
    @TableField(value = "status")
    private String status;

    @Schema(description = "父级ID")
    @TableField(value = "parent_id")
    private Long parentId;

    @Schema(description = "路由名称")
    @TableField(value = "route_name")
    private String routeName;

    @Schema(description = "路由路径")
    @TableField(value = "route_path")
    private String routePath;

    @Schema(description = "路由路径参数")
    @TableField(value = "route_path_param")
    private String routePathParam;

    @Schema(description = "权限标志")
    @TableField(value = "permission")
    private String permission;

    @Schema(description = "布局")
    @TableField(value = "layout")
    private String layout;

    @Schema(description = "组件")
    @TableField(value = "component")
    private String component;

    @Schema(description = "国际化KEY")
    @TableField(value = "i18n_key")
    private String i18nKey;

    @Schema(description = "显示顺序，越小越优先")
    @TableField(value = "sort_number")
    private Integer sortNumber;

    @Schema(description = "图标类型")
    @TableField(value = "icon_type")
    private String iconType;

    @Schema(description = "图标")
    @TableField(value = "icon")
    private String icon;

    @Schema(description = "缓存路由")
    @TableField(value = "cache_route")
    private Boolean cacheRoute;

    @Schema(description = "常量路由")
    @TableField(value = "constant_route")
    private Boolean constantRoute;

    @Schema(description = "隐藏路由")
    @TableField(value = "hide_route")
    private Boolean hideRoute;

    @Schema(description = "高亮路由名称")
    @TableField(value = "highlight_route_name")
    private String highlightRouteName;

    @Schema(description = "外链")
    @TableField(value = "external_link")
    private String externalLink;

    @Schema(description = "路由参数")
    @TableField(value = "route_param", typeHandler = MenuRouteParamListHandler.class)
    private List<MenuRouteParam> routeParam;

    @Schema(description = "按钮")
    @TableField(value = "button", typeHandler = MenuButtonListHandler.class)
    private List<MenuButton> button;
}

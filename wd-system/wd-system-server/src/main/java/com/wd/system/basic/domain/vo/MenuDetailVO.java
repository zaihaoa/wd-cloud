package com.wd.system.basic.domain.vo;

import com.wd.system.basic.domain.model.MenuButton;
import com.wd.system.basic.domain.model.MenuRouteParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 菜单详情
 * </p>
 *
 * @author huangwenda
 * @since 2024-01-08
 */
@Getter
@Setter
@Schema(description = "菜单详情")
public class MenuDetailVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "类型(DIRECTORY:目录,MENU:菜单)")
    private String menuType;

    @Schema(description = "类型描述", example = "目录")
    private String menuTypeDesc;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "所属分类")
    private String menuGroupName;

    @Schema(description = "状态(ENABLED:启用,DISABLED:禁用)")
    private String status;

    @Schema(description = "父级ID")
    private Long parentId;

    @Schema(description = "路由名称")
    private String routeName;

    @Schema(description = "路由路径")
    private String routePath;

    @Schema(description = "路由路径参数")
    private String routePathParam;

    @Schema(description = "权限标志")
    private String permission;

    @Schema(description = "布局")
    private String layout;

    @Schema(description = "组件")
    private String component;

    @Schema(description = "国际化KEY")
    private String i18nKey;

    @Schema(description = "显示顺序，越小越优先")
    private Integer sortNumber;

    @Schema(description = "图标类型")
    private String iconType;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "缓存路由")
    private Boolean cacheRoute;

    @Schema(description = "常量路由")
    private Boolean constantRoute;

    @Schema(description = "隐藏路由")
    private Boolean hideRoute;

    @Schema(description = "高亮路由名称")
    private String highlightRouteName;

    @Schema(description = "外链")
    private String externalLink;

    @Schema(description = "路由参数")
    private List<MenuRouteParam> routeParam;

    @Schema(description = "按钮")
    private List<MenuButton> button;

    @Schema(description = "创建人ID")
    private Long createUserId;

    @Schema(description = "更新人ID")
    private Long updateUserId;

    @Schema(description = "创建人名称")
    private String createUserName;

    @Schema(description = "更新人名称")
    private String updateUserName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

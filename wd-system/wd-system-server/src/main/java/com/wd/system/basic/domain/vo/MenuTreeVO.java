package com.wd.system.basic.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
@Schema(description = "菜单树形结构返回")
public class MenuTreeVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "菜单名称")
    private String name;

    @Schema(description = "父级ID")
    private Long parentId;

    @Schema(description = "显示顺序")
    private Integer sortNumber;

    @Schema(description = "类型(DIRECTORY:目录,MENU:菜单 BUTTON:按钮)", example = "DIRECTORY")
    private String menuType;

    @Schema(description = "类型描述(DIRECTORY:目录,MENU:菜单 BUTTON:按钮)", example = "目录")
    private String menuTypeDesc;

    @Schema(description = "路由地址")
    private String path;

    @Schema(description = "权限标志")
    private String permission;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "子列表")
    private List<MenuTreeVO> children;
}

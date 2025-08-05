package com.wd.system.basic.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wd.common.core.model.BaseUserTimeEntity;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

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
@TableName("sys_menu")
@Schema(name = "Menu", description = "菜单")
public class Menu extends BaseUserTimeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "菜单名称")
    @TableField("name")
    private String name;

    @Schema(description = "父级ID")
    @TableField("parent_id")
    private Long parentId;

    @Schema(description = "显示顺序,越小越优先")
    @TableField("sort_number")
    private Integer sortNumber;

    @Schema(description = "类型(DIRECTORY:目录,MENU:菜单 BUTTON:按钮)")
    @TableField("menu_type")
    private String menuType;

    @Schema(description = "路由地址")
    @TableField("path")
    private String path;

    @Schema(description = "权限标志")
    @TableField("permission")
    private String permission;

    @Schema(description = "菜单图标")
    @TableField("icon")
    private String icon;
}

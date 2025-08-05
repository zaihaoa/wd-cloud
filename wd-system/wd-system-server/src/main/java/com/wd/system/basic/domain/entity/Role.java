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
 * 角色
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@Getter
@Setter
@TableName("sys_role")
@Schema(name = "Role", description = "角色")
public class Role extends BaseUserTimeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "角色名称")
    @TableField("name")
    private String name;

    @Schema(description = "状态(NORMAL:正常,DISABLE:禁用)")
    @TableField("status")
    private String status;

    @Schema(description = "类型(NORMAL:普通,MANAGER:管理员,ADMIN:系统超级管理员)")
    @TableField("type")
    private String type;

    @Schema(description = "删除标记，等于0表示没有删除，否则为删除")
    @TableField("delete_flag")
    private Long deleteFlag;
}

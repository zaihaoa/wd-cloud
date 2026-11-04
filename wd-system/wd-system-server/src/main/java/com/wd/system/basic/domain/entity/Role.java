package com.wd.system.basic.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wd.common.core.model.BaseUserTimeEntity;
import com.wd.common.mybatisplus.handler.ListStringHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

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
@TableName(value = "sys_role", autoResultMap = true)
@Schema(name = "Role", description = "角色")
public class Role extends BaseUserTimeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "角色编码")
    @TableField("code")
    private String code;

    @Schema(description = "角色名称")
    @TableField("name")
    private String name;

    @Schema(description = "状态(ENABLED:启用,DISABLED:禁用)")
    @TableField("status")
    private String status;

    @Schema(description = "类型(NORMAL:普通,BUILT_IN:内置)")
    @TableField("type")
    private String type;

    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

    @Schema(description = "权限标记")
    @TableField(value = "permission_flag", typeHandler = ListStringHandler.class, updateStrategy = FieldStrategy.ALWAYS)
    private List<String> permissionFlag;

    @Schema(description = "删除标记，等于0表示没有删除，否则为删除")
    @TableField("delete_flag")
    @TableLogic(value = "0", delval = "id")
    private Long deleteFlag;
}

package com.wd.system.basic.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wd.common.core.model.BaseUserTimeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * 角色权限关联
 * </p>
 *
 * @author huangwenda
 * @since 2026-11-04
 */
@Getter
@Setter
@ToString
@TableName("sys_role_permission_relevance")
@Schema(name = "RolePermissionRelevance", description = "角色权限关联")
public class RolePermissionRelevance extends BaseUserTimeEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("role_id")
    @Schema(description = "角色ID")
    private Long roleId;

    @TableField("type")
    @Schema(description = "类型(MENU:菜单权限,FIELD:字段权限,DATA_USER:用户数据权限)")
    private String type;

    @TableField("relevance_id")
    @Schema(description = "关联ID")
    private Long relevanceId;

    @Schema(description = "关联值")
    @TableField("relevance_value")
    private String relevanceValue;
}

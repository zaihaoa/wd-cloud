package com.wd.system.basic.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 角色详情VO
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@Getter
@Setter
@Schema(description = "角色详情VO")
public class RoleDetailVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "角色编码")
    private String code;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "状态(ENABLED:启用,DISABLED:禁用)")
    private String status;

    @Schema(description = "状态描述(ENABLED:启用,DISABLED:禁用)")
    private String statusDesc;

    @Schema(description = "类型(NORMAL:普通,BUILT_IN:内置)")
    private String type;

    @Schema(description = "类型描述(NORMAL:普通,BUILT_IN:内置)")
    private String typeDesc;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "权限标记")
    private List<String> permissionFlag;

    @Schema(description = "创建人用户ID")
    private Long createUserId;

    @Schema(description = "更新人用户ID")
    private Long updateUserId;

    @Schema(description = "创建人姓名")
    private String createUserName;

    @Schema(description = "更新人姓名")
    private String updateUserName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "已授权菜单ID集合")
    private List<Long> menuIds;

    @Schema(description = "已授权按钮编码集合")
    private List<String> buttonCodes;

    @Schema(description = "已授权字段编码集合")
    private List<String> fieldCodes;

    @Schema(description = "已授权用户数据的用户ID集合")
    private List<Long> dataUserIds;
}

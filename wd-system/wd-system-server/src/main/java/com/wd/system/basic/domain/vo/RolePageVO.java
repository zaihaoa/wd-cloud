package com.wd.system.basic.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 角色分页VO
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@Getter
@Setter
@Schema(description = "角色列表VO")
public class RolePageVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "角色编码")
    private String code;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "状态(ENABLED:启用,DISABLED:禁用)")
    private String status;

    @Schema(description = "类型(NORMAL:普通,BUILT_IN:内置)")
    private String type;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "权限标记")
    private List<String> permissionFlag;

    @Schema(description = "删除标记，等于0表示没有删除，否则为删除")
    private Long deleteFlag;

    @Schema(description = "创建人用户ID")
    private Long createUserId;

    @Schema(description = "更新人用户ID")
    private Long updateUserId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

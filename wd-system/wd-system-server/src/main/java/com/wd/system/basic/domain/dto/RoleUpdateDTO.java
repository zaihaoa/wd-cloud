package com.wd.system.basic.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 角色更新DTO
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@Getter
@Setter
@Schema(description = "角色更新DTO")
public class RoleUpdateDTO {


    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "状态(NORMAL:正常,DISABLE:禁用)")
    private String status;

    @Schema(description = "类型(NORMAL:普通,MANAGER:管理员,ADMIN:系统超级管理员)")
    private String type;

    @Schema(description = "删除标记，等于0表示没有删除，否则为删除")
    private Long deleteFlag;
}

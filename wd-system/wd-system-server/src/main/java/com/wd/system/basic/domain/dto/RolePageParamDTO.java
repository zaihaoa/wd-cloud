package com.wd.system.basic.domain.dto;

import com.wd.common.core.model.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 角色 分页查询参数
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@Getter
@Setter
@Schema(description = "角色列表查询参数")
public class RolePageParamDTO extends BaseQuery {

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "状态(NORMAL:正常,DISABLE:禁用)")
    private String status;

    @Schema(description = "类型(NORMAL:普通,MANAGER:管理员,ADMIN:系统超级管理员)")
    private String type;
}

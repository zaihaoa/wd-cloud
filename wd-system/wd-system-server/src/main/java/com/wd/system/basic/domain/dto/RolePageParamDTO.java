package com.wd.system.basic.domain.dto;

import com.wd.common.core.model.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
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

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "角色编码")
    @Size(max = 100, message = "角色编码长度不能超过100")
    private String code;

    @Schema(description = "角色名称")
    @Size(max = 100, message = "角色名称长度不能超过100")
    private String name;

    @Schema(description = "状态(ENABLED:启用,DISABLED:禁用)")
    private String status;

    @Schema(description = "类型(NORMAL:普通,BUILT_IN:内置)")
    private String type;
}

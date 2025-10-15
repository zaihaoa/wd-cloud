package com.wd.system.basic.domain.dto;

import com.wd.common.core.model.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 全局配置 分页查询参数
 * </p>
 *
 * @author huangwenda
 * @since 2025-10-11
 */
@Getter
@Setter
@Schema(description = "全局配置列表查询参数")
public class GlobalConfigPageParamDTO extends BaseQuery {

    @Schema(description = "编码")
    private String code;

    @Schema(description = "值")
    private String value;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "类型(SYSTEM:系统内置,NORMAL:普通)")
    private String type;
}

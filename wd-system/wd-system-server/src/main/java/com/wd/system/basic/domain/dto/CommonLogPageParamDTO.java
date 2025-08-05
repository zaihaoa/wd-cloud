package com.wd.system.basic.domain.dto;

import com.wd.common.core.model.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 通用日志 分页查询参数
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@Getter
@Setter
@Schema(description = "通用日志列表查询参数")
public class CommonLogPageParamDTO extends BaseQuery {

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "业务ID")
    private Long businessId;
}

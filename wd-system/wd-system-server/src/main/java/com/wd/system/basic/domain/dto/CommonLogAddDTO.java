package com.wd.system.basic.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * <p>
 * 通用日志新增DTO
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "通用日志添加DTO")
public class CommonLogAddDTO {

    @Schema(description = "供应商ID")
    private Long supplierId;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "业务ID")
    private Long businessId;

    @Schema(description = "操作类型")
    private String operationType;

    @Schema(description = "操作内容")
    private String content;
}

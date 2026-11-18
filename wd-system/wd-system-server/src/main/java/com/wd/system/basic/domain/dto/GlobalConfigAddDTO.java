package com.wd.system.basic.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 全局配置新增DTO
 * </p>
 *
 * @author huangwenda
 * @since 2025-10-11
 */
@Getter
@Setter
@Schema(description = "全局配置新增DTO")
public class GlobalConfigAddDTO {

    @Schema(description = "编码")
    @NotBlank(message = "编码不能为空")
    private String code;

    @Schema(description = "值")
    private String value;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "显示顺序，越小越优先")
    private Integer sortNumber;
}

package com.wd.system.basic.domain.dto;

import com.wd.common.core.objectcompare.ObjectCompare;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 全局配置更新DTO
 * </p>
 *
 * @author huangwenda
 * @since 2025-10-11
 */
@Getter
@Setter
@Schema(description = "全局配置更新DTO")
public class GlobalConfigUpdateDTO {

    @Schema(description = "主键ID")
    @NotNull(message = "主键ID不能为空")
    private Long id;

    @Schema(description = "值")
    @ObjectCompare("参数值")
    private String value;

    @Schema(description = "备注")
    @ObjectCompare("备注")
    private String remark;

    @Schema(description = "显示顺序，越小越优先")
    @ObjectCompare("显示顺序")
    private Integer sortNumber;
}

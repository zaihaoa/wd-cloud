package com.wd.system.basic.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 *
 *
 * @author huangwenda
 * @date 2025/9/24 17:50
 **/
@Getter
@Setter
public class UserConfigQueryDTO {

    @Schema(description = "类型(CUSTOM_COLUMN:自定义列,QUERY:查询条件)")
    @NotBlank(message = "类型不能为空")
    private String type;

    @Schema(description = "编码")
    @NotBlank(message = "编码不能为空")
    private String code;
}

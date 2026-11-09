package com.wd.system.basic.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class UserConfigAddUpdateDTO {

    @Schema(description = "类型(CUSTOM_COLUMN:自定义列,QUERY:查询条件)")
    @NotBlank(message = "类型不能为空")
    @Size(max = 100, message = "类型长度不能超过100")
    private String type;

    @Schema(description = "编码")
    @NotBlank(message = "编码不能为空")
    @Size(max = 500, message = "编码长度不能超过500")
    private String code;

    @Schema(description = "内容")
    @NotBlank(message = "内容不能为空")
    private String content;
}

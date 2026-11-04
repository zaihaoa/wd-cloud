package com.wd.system.basic.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 菜单按钮
 */
@Data
@Schema(description = "菜单按钮")
public class MenuButton implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "按钮编码")
    private String code;

    @Schema(description = "按钮名称")
    private String label;
}

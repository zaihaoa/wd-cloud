package com.wd.system.basic.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 菜单路由参数
 */
@Data
@Schema(description = "菜单路由参数")
public class MenuRouteParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "参数名")
    private String param;

    @Schema(description = "参数值")
    private String value;
}

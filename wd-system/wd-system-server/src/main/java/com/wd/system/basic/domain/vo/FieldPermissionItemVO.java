package com.wd.system.basic.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字段权限项。
 */
@Getter
@AllArgsConstructor
@Schema(description = "字段权限项")
public class FieldPermissionItemVO {

    @Schema(description = "字段权限编码")
    private String code;

    @Schema(description = "字段权限名称")
    private String label;
}

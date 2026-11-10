package com.wd.system.basic.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 字段权限分组。
 */
@Getter
@AllArgsConstructor
@Schema(description = "字段权限分组")
public class FieldPermissionGroupVO {

    @Schema(description = "分组名称")
    private String groupName;

    @Schema(description = "字段权限项")
    private List<FieldPermissionItemVO> items;
}

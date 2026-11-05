package com.wd.system.system.api.basic.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "用户绑定的角色")
public class UserRoleVO {

    @Schema(description = "角色ID")
    private Long ruleId;

    @Schema(description = "角色编码")
    private String ruleCode;

    @Schema(description = "角色名称")
    private String ruleName;
}

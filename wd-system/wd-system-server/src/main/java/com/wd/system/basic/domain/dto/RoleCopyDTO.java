package com.wd.system.basic.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "角色复制DTO")
public class RoleCopyDTO extends RoleAddDTO {

    @Schema(description = "复制的角色ID")
    @NotNull(message = "角色ID不能为空")
    private Long roleId;
}

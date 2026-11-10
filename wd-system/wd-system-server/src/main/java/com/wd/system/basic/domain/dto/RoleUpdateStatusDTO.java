package com.wd.system.basic.domain.dto;

import com.wd.common.core.annotions.EnumCodeExist;
import com.wd.system.basic.domain.enums.RoleStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "角色状态更新DTO")
public class RoleUpdateStatusDTO {

    @Schema(description = "ID", example = "123")
    @NotNull(message = "id not null")
    private Long id;

    @Schema(description = "角色状态", allowableValues = {"ENABLED", "DISABLED"})
    @EnumCodeExist(enumClass = RoleStatusEnum.class, required = true, message = "状态不正确")
    private String status;
}

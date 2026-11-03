package com.wd.system.basic.domain.dto;

import com.wd.common.core.annotions.EnumCodeExist;
import com.wd.system.basic.domain.enums.UserStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * @author huangwenda
 * @date 2024/1/9 13:48
 **/
@Getter
@Setter
@Schema(description = "用户更新状态DTO")
public class UserUpdateStatusDTO {

    @Schema(description = "ID")
    @NotNull(message = "ID不能为空")
    private Long id;

    @Schema(description = "状态(NORMAL:启用,DISABLE:禁用)")
    @EnumCodeExist(enumClass = UserStatusEnum.class, required = true, message = "状态不正确")
    private String status;
}

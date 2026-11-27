package com.wd.system.basic.domain.dto;

import com.wd.common.core.annotions.EnumCodeExist;
import com.wd.system.basic.domain.enums.UserUpdateCurrentTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "修改当前用户信息DTO")
public class UserUpdateCurrentDTO {

    @Schema(description = "修改类型(NAME:姓名,PHONE:手机号,EMAIL:邮箱)")
    @EnumCodeExist(enumClass = UserUpdateCurrentTypeEnum.class, required = true, message = "修改类型不正确")
    private String updateType;

    @Schema(description = "姓名，修改类型为NAME时必填")
    private String name;

    @Schema(description = "手机号，修改类型为PHONE时生效，空值表示清空")
    private String phone;

    @Schema(description = "邮箱，修改类型为EMAIL时生效，空值表示清空")
    private String email;
}

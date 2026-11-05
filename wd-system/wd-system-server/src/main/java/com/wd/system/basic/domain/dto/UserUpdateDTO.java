package com.wd.system.basic.domain.dto;

import com.wd.common.core.objectcompare.ObjectCompare;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * <p>
 * 用户更新DTO
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@Getter
@Setter
@Schema(description = "用户更新DTO")
public class UserUpdateDTO {

    @Schema(description = "主键ID")
    @NotNull(message = "主键ID不能为空")
    private Long id;

    @Schema(description = "用户名")
    @ObjectCompare("用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "姓名")
    @ObjectCompare("姓名")
    @NotBlank(message = "姓名不能为空")
    private String name;

    @Schema(description = "手机号码")
    @ObjectCompare("手机号码")
    private String phone;

    @Schema(description = "邮箱")
    @ObjectCompare("邮箱")
    private String email;

    @Schema(description = "角色ID集合，必填且至少包含一个角色，覆盖设置用户角色")
    @NotEmpty(message = "角色ID集合不能为空")
    private List<@NotNull(message = "角色ID不能为空") @Positive(message = "角色ID必须为正数") Long> roleIds;
}

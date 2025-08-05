package com.wd.system.basic.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户新增DTO
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@Getter
@Setter
@Schema(description = "用户新增DTO")
public class UserAddDTO {


    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "用户密码")
    private String password;

    @Schema(description = "状态(NORMAL:正常,DISABLE:禁用)")
    private String status;

    @Schema(description = "类型(BUILT_IN:内置用户,NORMAL:普通用户)")
    private String type;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "删除标记，等于0表示没有删除，否则为删除")
    private Long deleteFlag;
}

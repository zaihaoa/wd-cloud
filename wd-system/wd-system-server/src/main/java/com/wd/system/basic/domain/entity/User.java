package com.wd.system.basic.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wd.common.core.model.BaseUserTimeEntity;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@Getter
@Setter
@TableName("sys_user")
@Schema(name = "User", description = "用户")
public class User extends BaseUserTimeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "用户名")
    @TableField("username")
    private String username;

    @Schema(description = "姓名")
    @TableField("name")
    private String name;

    @Schema(description = "用户密码")
    @TableField("password")
    private String password;

    @Schema(description = "状态(NORMAL:正常,DISABLE:禁用)")
    @TableField("status")
    private String status;

    @Schema(description = "类型(BUILT_IN:内置用户,NORMAL:普通用户)")
    @TableField("type")
    private String type;

    @Schema(description = "手机号码")
    @TableField("phone")
    private String phone;

    @Schema(description = "邮箱")
    @TableField("email")
    private String email;

    @Schema(description = "删除标记，等于0表示没有删除，否则为删除")
    @TableField("delete_flag")
    @TableLogic(value = "0")
    private Long deleteFlag;
}

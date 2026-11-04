package com.wd.system.basic.domain.vo;

import com.wd.common.core.annotions.FieldPermission;
import com.wd.common.core.constants.FieldPermissionConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户分页VO
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@Getter
@Setter
@Schema(description = "用户列表VO")
public class UserPageVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "状态(NORMAL:正常,DISABLE:禁用)")
    private String status;

    @Schema(description = "状态描述(NORMAL:正常,DISABLE:禁用)")
    private String statusDesc;

    @Schema(description = "类型(BUILT_IN:内置用户,NORMAL:普通用户)")
    private String type;

    @Schema(description = "类型描述(BUILT_IN:内置用户,NORMAL:普通用户)")
    private String typeDesc;

    @Schema(description = "手机号码")
    @FieldPermission(FieldPermissionConstant.USER_PHONE)
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "创建人ID")
    private Long createUserId;

    @Schema(description = "更新人ID")
    private Long updateUserId;

    @Schema(description = "创建人姓名")
    private String createUserName;

    @Schema(description = "更新人姓名")
    private String updateUserName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

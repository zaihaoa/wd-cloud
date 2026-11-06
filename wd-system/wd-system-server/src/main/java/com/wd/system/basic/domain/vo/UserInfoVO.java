package com.wd.system.basic.domain.vo;

import com.wd.system.system.api.basic.vo.UserRoleVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
@Schema(description = "用户信息VO")
public class UserInfoVO {

    @Schema(description = "用户ID")
    private Long userId;

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
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "创建人ID")
    private Long createUserId;

    @Schema(description = "更新人ID")
    private Long updateUserId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "用户绑定的角色集合")
    private List<UserRoleVO> roles;

    @Schema(description = "是否为ADMIN用户")
    private Boolean adminUser;

    @Schema(description = "是否是ADMIN角色")
    private Boolean adminRole;

    @Schema(description = "有权限的菜单ID权限(返回有的权限集合,没有权限返回空数组)")
    private List<Long> authMenuIds;

    @Schema(description = "有权限的菜单按钮权限(返回有的权限集合,没有权限返回空数组)")
    private List<String> authMenuPermissionList;

    @Schema(description = "有权限的字段权限(返回有的权限集合,没有权限返回空数组)")
    private List<String> authFieldPermissionList;

    @Schema(description = "有权限的菜单")
    private List<MenuTreeVO> authMenuList;

}

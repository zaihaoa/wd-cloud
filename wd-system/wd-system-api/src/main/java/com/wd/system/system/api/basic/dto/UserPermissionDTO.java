package com.wd.system.system.api.basic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author huangwenda
 * @date 2024/1/9 13:48
 **/
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "用户权限DTO")
public class UserPermissionDTO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "状态(NORMAL:启用,DISABLE:禁用) UserStatusEnum")
    private String status;

    @Schema(description = "类型(BUILT_IN:内置用户,NORMAL:普通用户) UserTypeEnum")
    private String type;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "是否为ADMIN用户")
    private Boolean adminUser;

    @Schema(description = "是否是ADMIN角色")
    private Boolean adminRole;

    @Schema(description = "角色ID集合")
    private List<Long> roleIds;

    @Schema(description = "数据权限ID集合")
    private List<Long> dataPermissionIds;

    @Schema(description = "有权限的菜单ID权限(返回有的权限集合,没有权限返回null或者空数组)")
    private List<Long> authMenuIds;

    @Schema(description = "有权限的菜单按钮权限(返回有的权限集合,没有权限返回null或者空数组)")
    private List<String> authMenuPermissionList;

    @Schema(description = "有权限的字段权限(返回有的权限集合,没有权限返回null或者空数组)")
    private List<String> authFieldPermissionList;


    /**
     * 是否拥有所有菜单权限
     */
    public boolean allMenuPermission() {
        List<Long> menuIds = getAuthMenuIds();
        return menuIds == null || menuIds.isEmpty();
    }

}

package com.wd.system.basic.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wd.common.core.constants.CommonConstant;
import com.wd.common.core.context.SystemContext;
import com.wd.common.core.enums.FieldPermissionEnum;
import com.wd.common.core.util.AssertUtil;
import com.wd.common.redis.CacheRegion;
import com.wd.common.redis.RedisKey;
import com.wd.common.redis.util.RedisHelper;
import com.wd.system.basic.convert.UserConverter;
import com.wd.system.basic.domain.entity.*;
import com.wd.system.basic.domain.enums.*;
import com.wd.system.basic.mapper.*;
import com.wd.system.basic.service.PermissionService;
import com.wd.system.system.api.basic.dto.UserPermissionDTO;
import com.wd.system.basic.util.MenuButtonHelper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 用户有效权限汇总。
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleRelevanceMapper userRoleRelevanceMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RolePermissionRelevanceMapper rolePermissionRelevanceMapper;
    @Resource
    private MenuMapper menuMapper;
    @Resource
    private UserConverter userConverter;
    @Resource
    private RedisHelper redisHelper;

    @Override
    public UserPermissionDTO getUserPermission(Long userId) {
        AssertUtil.isTrue(userId != null && userId >= 0, "用户ID不能为空或负数");
        return loadUserPermission(userId);
    }

    private UserPermissionDTO loadUserPermission(Long userId) {
        String key = RedisHelper.joinKey(RedisKey.USER_PERMISSION, userId);
        UserPermissionDTO cached = redisHelper.get(CacheRegion.PERMISSION, key, new TypeReference<>() {
        });
        if (cached != null) {
            return cached;
        }

        User user = userMapper.selectById(userId);
        AssertUtil.notNull(user, "用户不存在或已删除");
        AssertUtil.isTrue(UserStatusEnum.ENABLED.getCode().equals(user.getStatus()), "用户已被禁用");

        List<Role> roles = effectiveRoles(userId);
        UserPermissionDTO result = userConverter.convertUserPermissionDTO(user);
        result.setAdminUser(Objects.equals(userId, CommonConstant.ADMIN_USER_ID));
        result.setAdminRole(roles.stream().anyMatch(role -> RoleBuiltInCodeEnum.ADMIN.getCode().equals(role.getCode())));
        result.setRoleList(roles.stream().map(userConverter::convertUserRoleVO).toList());

        Set<String> flags = roles.stream()
                .map(Role::getPermissionFlag)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        boolean all = result.getAdminUser() || result.getAdminRole()
                || flags.contains(RolePermissionFlagEnum.ALL_PERMISSION.getCode());
        List<RolePermissionRelevance> permissions = roles.isEmpty() ? List.of()
                : rolePermissionRelevanceMapper.selectList(Wrappers.<RolePermissionRelevance>lambdaQuery()
                .in(RolePermissionRelevance::getRoleId, roles.stream().map(Role::getId).toList())
                .orderByAsc(RolePermissionRelevance::getId));

        boolean allMenus = all || flags.contains(RolePermissionFlagEnum.ALL_MENU_PERMISSION.getCode());
        List<Menu> menus = authorizedMenus(allMenus, permissions);
        result.setAuthMenuIds(menus.stream().map(Menu::getId).toList());
        result.setAuthMenuPermissionList(Stream.concat(
                menus.stream().map(Menu::getPermission).filter(StringUtils::isNotBlank),
                authorizedButtonCodes(menus, permissions, allMenus))
                .distinct().toList());
        result.setAuthFieldPermissionList(all || flags.contains(RolePermissionFlagEnum.ALL_FIELD_PERMISSION.getCode())
                ? Arrays.stream(FieldPermissionEnum.values()).map(FieldPermissionEnum::getCode).toList()
                : permissions.stream().filter(p -> RolePermissionTypeEnum.FIELD.getCode().equals(p.getType()))
                .map(RolePermissionRelevance::getRelevanceValue).filter(StringUtils::isNotBlank).distinct().toList());
        result.setAuthUserIds(all || flags.contains(RolePermissionFlagEnum.ALL_DATA_USER_PERMISSION.getCode())
                ? null : authorizedUserIds(permissions));

        redisHelper.setEx(CacheRegion.PERMISSION, key, result, 5, TimeUnit.DAYS);
        return result;
    }

    private List<Role> effectiveRoles(Long userId) {
        List<Long> roleIds = userRoleRelevanceMapper.selectList(Wrappers.<UserRoleRelevance>lambdaQuery()
                        .eq(UserRoleRelevance::getUserId, userId)
                        .select(UserRoleRelevance::getRoleId))
                .stream().map(UserRoleRelevance::getRoleId).filter(Objects::nonNull).distinct().toList();
        if (roleIds.isEmpty()) {
            return List.of();
        }
        return roleMapper.selectList(Wrappers.<Role>lambdaQuery()
                .in(Role::getId, roleIds)
                .eq(Role::getStatus, RoleStatusEnum.ENABLED.getCode())
                .orderByAsc(Role::getId));
    }

    private List<Menu> authorizedMenus(boolean all, List<RolePermissionRelevance> permissions) {
        List<Long> ids = permissionIds(permissions, RolePermissionTypeEnum.MENU);
        if (!all && ids.isEmpty()) {
            return List.of();
        }
        return menuMapper.selectList(Wrappers.<Menu>lambdaQuery()
                .eq(Menu::getStatus, MenuStatusEnum.ENABLED.getCode())
                .in(!all, Menu::getId, ids)
                .select(Menu::getId, Menu::getPermission, Menu::getButton)
                .orderByAsc(Menu::getId));
    }

    private Stream<String> authorizedButtonCodes(List<Menu> menus, List<RolePermissionRelevance> permissions, boolean allMenus) {
        if (allMenus) {
            return menus.stream().flatMap(menu -> MenuButtonHelper.permissionCodes(menu.getButton()));
        }

        Map<Long, Menu> menusById = menus.stream().collect(Collectors.toMap(Menu::getId, menu -> menu));
        Map<Long, Set<String>> availableCodesByRole = permissions.stream()
                .filter(p -> RolePermissionTypeEnum.MENU.getCode().equals(p.getType()))
                .filter(p -> menusById.containsKey(p.getRelevanceId()))
                .collect(Collectors.groupingBy(RolePermissionRelevance::getRoleId,
                        Collectors.flatMapping(p -> MenuButtonHelper.permissionCodes(menusById.get(p.getRelevanceId()).getButton()),
                                Collectors.toSet())));
        return permissions.stream()
                .filter(p -> RolePermissionTypeEnum.BUTTON.getCode().equals(p.getType()))
                .filter(p -> p.getRelevanceValue() != null)
                .filter(p -> availableCodesByRole.getOrDefault(p.getRoleId(), Set.of()).contains(p.getRelevanceValue()))
                .map(RolePermissionRelevance::getRelevanceValue);
    }

    private List<Long> authorizedUserIds(List<RolePermissionRelevance> permissions) {
        List<Long> ids = permissionIds(permissions, RolePermissionTypeEnum.DATA_USER);
        if (ids.isEmpty()) {
            return List.of(CommonConstant.NOT_PERMISSION_FLAG);
        }
        List<Long> userIds = userMapper.selectList(Wrappers.<User>lambdaQuery()
                        .in(User::getId, ids).select(User::getId).orderByAsc(User::getId))
                .stream().map(User::getId).toList();
        return userIds.isEmpty() ? List.of(CommonConstant.NOT_PERMISSION_FLAG) : userIds;
    }

    private List<Long> permissionIds(List<RolePermissionRelevance> permissions, RolePermissionTypeEnum type) {
        return permissions.stream().filter(p -> type.getCode().equals(p.getType()))
                .map(RolePermissionRelevance::getRelevanceId)
                .filter(id -> id != null && id >= 0).distinct().toList();
    }

    @Override
    public UserPermissionDTO getCurrentUserPermission() {
        return getUserPermission(SystemContext.getUserIdAssertExist());
    }
}

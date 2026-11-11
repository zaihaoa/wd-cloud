package com.wd.system.basic.service.impl;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.wd.common.core.exception.PromptException;
import com.wd.common.redis.util.RedisHelper;
import com.wd.system.basic.convert.UserConverter;
import com.wd.system.basic.domain.dto.RoleAuthorizeDTO;
import com.wd.system.basic.domain.entity.Menu;
import com.wd.system.basic.domain.entity.Role;
import com.wd.system.basic.domain.entity.RolePermissionRelevance;
import com.wd.system.basic.domain.entity.User;
import com.wd.system.basic.domain.entity.UserRoleRelevance;
import com.wd.system.basic.domain.model.MenuButton;
import com.wd.system.basic.mapper.MenuMapper;
import com.wd.system.basic.mapper.RoleMapper;
import com.wd.system.basic.mapper.RolePermissionRelevanceMapper;
import com.wd.system.basic.mapper.UserMapper;
import com.wd.system.basic.mapper.UserRoleRelevanceMapper;
import com.wd.system.system.api.basic.dto.UserPermissionDTO;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class RoleAuthorizationTest {
    private final RoleServiceImpl service = new RoleServiceImpl();
    private final RolePermissionRelevanceMapper permissions = mock(RolePermissionRelevanceMapper.class);
    private final JsonMapper json = JsonMapper.builder().build();

    @BeforeEach
    void setUp() {
        var roles = mock(RoleMapper.class);
        var menus = mock(MenuMapper.class);
        var users = mock(UserMapper.class);
        ReflectionTestUtils.setField(service, "baseMapper", roles);
        ReflectionTestUtils.setField(service, "menuMapper", menus);
        ReflectionTestUtils.setField(service, "userMapper", users);
        ReflectionTestUtils.setField(service, "rolePermissionRelevanceMapper", permissions);
        ReflectionTestUtils.setField(service, "redisHelper", mock(RedisHelper.class));
        when(roles.selectOne(any())).thenReturn(new Role());
        var button = new MenuButton();
        button.setCode("orders:edit");
        var menu = new Menu();
        menu.setId(8L);
        menu.setButton(List.of(button));
        when(menus.selectCount(any())).thenReturn(1L);
        when(menus.selectList(any())).thenReturn(List.of(menu));
        when(users.selectCount(any())).thenReturn(1L);
        when(permissions.insertBatchSomeColumn(any())).thenAnswer(invocation ->
                invocation.<List<RolePermissionRelevance>>getArgument(0).size());
    }

    @Test
    void storesMenuAndButtonGrantsInTheirRespectiveTypes() {
        assertTrue(service.authorize(request("""
                {"roleId":2,"type":"MENU","relevanceIds":[8],"relevanceValues":["orders:edit"]}
                """)));
        verify(permissions).insertBatchSomeColumn(argThat(rows -> rows.stream()
                .anyMatch(row -> "BUTTON".equals(row.getType()) && "orders:edit".equals(row.getRelevanceValue()))));
        verify(permissions).insertBatchSomeColumn(argThat(rows -> rows.stream()
                .anyMatch(row -> "MENU".equals(row.getType()) && Long.valueOf(8).equals(row.getRelevanceId()))));
    }

    @Test
    void rejectsUnknownButtonBeforeReplacingMenus() {
        assertThrows(PromptException.class, () -> service.authorize(request("""
                {"roleId":2,"type":"MENU","relevanceIds":[8],"relevanceValues":["*"]}
                """)));
        verifyNoInteractions(permissions);
    }

    @Test
    void clearsMenusAndButtonsWhenBothCollectionsAreEmpty() {
        var assistant = new MapperBuilderAssistant(new MybatisConfiguration(), "role-authorization-test");
        TableInfoHelper.initTableInfo(assistant, RolePermissionRelevance.class);
        var clearedTypes = new ArrayList<>();
        when(permissions.delete(any())).thenAnswer(invocation -> {
            var query = invocation.<LambdaQueryWrapper<?>>getArgument(0);
            query.getSqlSegment();
            clearedTypes.addAll(query.getParamNameValuePairs().values());
            return 0;
        });

        assertTrue(service.authorize(request("""
                {"roleId":2,"type":"MENU","relevanceIds":[],"relevanceValues":[]}
                """)));
        assertTrue(clearedTypes.containsAll(List.of("MENU", "BUTTON")));
        verify(permissions, never()).insertBatchSomeColumn(any());
    }

    @Test
    void rejectsIncompleteMenuPayloadBeforeReplacingAnything() {
        assertThrows(PromptException.class, () -> service.authorize(request("""
                {"roleId":2,"type":"MENU","relevanceIds":[8]}
                """)));
        verifyNoInteractions(permissions);
    }

    @Test
    void rejectsInvalidOperationNamesAndOrdinals() {
        for (String operation : List.of("\"REPLACE\"", "0", "\"0\"")) {
            assertThrows(JacksonException.class, () -> request("""
                    {"roleId":2,"type":"DATA_USER","operation":%s,"relevanceIds":[9]}
                    """.formatted(operation)));
        }
    }

    @Test
    void grantsOnlyMissingUsersAndAllowsRetry() {
        var existing = new RolePermissionRelevance();
        existing.setRelevanceId(9L);
        when(permissions.selectList(any())).thenReturn(List.of(), List.of(existing));
        var request = request("""
                {"roleId":2,"type":"DATA_USER","operation":"GRANT","relevanceIds":[9,9]}
                """);

        assertTrue(service.authorize(request));
        assertTrue(service.authorize(request));
        verify(permissions, never()).delete(any());
        verify(permissions).insertBatchSomeColumn(argThat(rows -> rows.size() == 1 && rows.stream()
                .allMatch(row -> "DATA_USER".equals(row.getType()) && Long.valueOf(9).equals(row.getRelevanceId()))));
    }

    @Test
    void revokesOnlySelectedUsersAndAllowsRetry() {
        var assistant = new MapperBuilderAssistant(new MybatisConfiguration(), "role-authorization-test");
        TableInfoHelper.initTableInfo(assistant, RolePermissionRelevance.class);
        when(permissions.delete(any())).thenAnswer(invocation -> {
            var query = invocation.<LambdaQueryWrapper<?>>getArgument(0);
            assertTrue(query.getSqlSegment().contains("relevance_id IN"));
            assertTrue(query.getParamNameValuePairs().values().containsAll(List.of(2L, "DATA_USER", 9L)));
            return 0;
        });
        var request = request("""
                {"roleId":2,"type":"DATA_USER","operation":"REVOKE","relevanceIds":[9]}
                """);

        assertTrue(service.authorize(request));
        assertTrue(service.authorize(request));
        verify(permissions, times(2)).delete(any());
        verify(permissions, never()).insertBatchSomeColumn(any());
    }

    @Test
    void rejectsMissingOperationAndEmptySelection() {
        for (String body : List.of(
                """
                {"roleId":2,"type":"DATA_USER","relevanceIds":[9]}
                """,
                """
                {"roleId":2,"type":"DATA_USER","operation":"REVOKE","relevanceIds":[]}
                """,
                """
                {"roleId":2,"type":"MENU","operation":"REVOKE","relevanceIds":[8],"relevanceValues":[]}
                """)) {
            assertThrows(PromptException.class, () -> service.authorize(request(body)));
        }
        verifyNoInteractions(permissions);
    }

    @Test
    void buttonPermissionRequiresItsOwnRoleToGrantTheMenuUnlessAllMenusAreEnabled() {
        var assistant = new MapperBuilderAssistant(new MybatisConfiguration(), "role-authorization-test");
        TableInfoHelper.initTableInfo(assistant, UserRoleRelevance.class);
        TableInfoHelper.initTableInfo(assistant, Menu.class);
        var permissionService = new PermissionServiceImpl();
        var users = mock(UserMapper.class);
        var userRoles = mock(UserRoleRelevanceMapper.class);
        var roles = mock(RoleMapper.class);
        var menus = mock(MenuMapper.class);
        var converter = mock(UserConverter.class);
        ReflectionTestUtils.setField(permissionService, "userMapper", users);
        ReflectionTestUtils.setField(permissionService, "userRoleRelevanceMapper", userRoles);
        ReflectionTestUtils.setField(permissionService, "roleMapper", roles);
        ReflectionTestUtils.setField(permissionService, "rolePermissionRelevanceMapper", permissions);
        ReflectionTestUtils.setField(permissionService, "menuMapper", menus);
        ReflectionTestUtils.setField(permissionService, "userConverter", converter);
        ReflectionTestUtils.setField(permissionService, "redisHelper", mock(RedisHelper.class));

        var user = new User();
        user.setStatus("ENABLED");
        var firstRole = new Role();
        firstRole.setId(2L);
        var secondRole = new Role();
        secondRole.setId(3L);
        var firstAssignment = new UserRoleRelevance();
        firstAssignment.setRoleId(2L);
        var secondAssignment = new UserRoleRelevance();
        secondAssignment.setRoleId(3L);
        var menu = new Menu();
        menu.setId(8L);
        menu.setPermission("orders:view");
        var button = new MenuButton();
        button.setCode("orders:edit");
        menu.setButton(List.of(button));
        when(users.selectById(50L)).thenReturn(user);
        when(userRoles.selectList(any())).thenReturn(List.of(firstAssignment, secondAssignment));
        when(roles.selectList(any())).thenReturn(List.of(firstRole, secondRole));
        when(menus.selectList(any())).thenReturn(List.of(menu));
        when(converter.convertUserPermissionDTO(user)).thenAnswer(invocation -> new UserPermissionDTO());

        var menuGrant = new RolePermissionRelevance();
        menuGrant.setRoleId(2L);
        menuGrant.setType("MENU");
        menuGrant.setRelevanceId(8L);
        var buttonGrant = new RolePermissionRelevance();
        buttonGrant.setRoleId(3L);
        buttonGrant.setType("BUTTON");
        buttonGrant.setRelevanceValue("orders:edit");
        when(permissions.selectList(any())).thenReturn(List.of(menuGrant, buttonGrant));

        assertEquals(List.of("orders:view"), permissionService.getUserPermission(50L).getAuthMenuPermissionList());
        buttonGrant.setRoleId(2L);
        assertEquals(List.of("orders:view", "orders:edit"), permissionService.getUserPermission(50L).getAuthMenuPermissionList());
        when(permissions.selectList(any())).thenReturn(List.of(menuGrant));
        assertEquals(List.of("orders:view"), permissionService.getUserPermission(50L).getAuthMenuPermissionList());
        firstRole.setPermissionFlag(List.of("ALL_MENU_PERMISSION"));
        assertEquals(List.of("orders:view", "orders:edit"), permissionService.getUserPermission(50L).getAuthMenuPermissionList());
    }

    private RoleAuthorizeDTO request(String body) {
        return json.readValue(body, RoleAuthorizeDTO.class);
    }
}

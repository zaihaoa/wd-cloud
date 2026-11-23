package com.wd.system.basic.service.impl;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.wd.common.core.constants.FieldPermissionConstant;
import com.wd.common.core.exception.PromptException;
import com.wd.common.redis.CacheRegion;
import com.wd.common.redis.util.RedisHelper;
import com.wd.system.basic.convert.UserConverter;
import com.wd.system.basic.controller.RoleController;
import com.wd.system.basic.domain.dto.RoleAuthorizeAllDTO;
import com.wd.system.basic.domain.dto.RoleAuthorizeDataUserDTO;
import com.wd.system.basic.domain.dto.RoleAuthorizeFieldDTO;
import com.wd.system.basic.domain.dto.RoleAuthorizeMenuDTO;
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
import com.wd.system.basic.service.CommonLogService;
import com.wd.system.system.api.basic.dto.UserPermissionDTO;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RoleAuthorizationTest {
    private final RoleServiceImpl service = new RoleServiceImpl();
    private final RoleMapper roles = mock(RoleMapper.class);
    private final RolePermissionRelevanceMapper permissions = mock(RolePermissionRelevanceMapper.class);
    private final MenuMapper menus = mock(MenuMapper.class);
    private final UserMapper users = mock(UserMapper.class);
    private final CommonLogService commonLogs = mock(CommonLogService.class);
    private final RedisHelper redis = mock(RedisHelper.class);
    private final JsonMapper json = JsonMapper.builder().build();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "baseMapper", roles);
        ReflectionTestUtils.setField(service, "menuMapper", menus);
        ReflectionTestUtils.setField(service, "userMapper", users);
        ReflectionTestUtils.setField(service, "rolePermissionRelevanceMapper", permissions);
        ReflectionTestUtils.setField(service, "redisHelper", redis);
        ReflectionTestUtils.setField(service, "commonLogService", commonLogs);
        var role = new Role();
        role.setId(2L);
        when(roles.selectById(2L)).thenReturn(role);
        when(commonLogs.addLog(any())).thenReturn(true);
        when(menus.selectList(any())).thenReturn(List.of(menu(8L, "订单管理", "orders:edit")));
        when(users.selectCount(any())).thenReturn(1L);
        when(permissions.insertBatchSomeColumn(any())).thenAnswer(invocation ->
                invocation.<List<RolePermissionRelevance>>getArgument(0).size());
    }

    @Test
    void copiesAllRoleGrantsAndAuditsOnlyTheSource() throws Exception {
        var source = new Role();
        source.setId(2L);
        source.setCode("source");
        source.setName("原角色");
        source.setStatus("DISABLED");
        source.setType("BUILT_IN");
        source.setPermissionFlag(List.of("ALL_MENU_PERMISSION", "ALL_DATA_USER_PERMISSION"));
        when(roles.selectById(2L)).thenReturn(source);
        when(roles.insert(any(Role.class))).thenReturn(1);

        var grants = List.of(permission(8L, ""), permission(0L, "orders:edit"),
                permission(0L, FieldPermissionConstant.USER_PHONE), permission(9L, ""));
        var types = List.of("MENU", "BUTTON", "FIELD", "DATA_USER");
        for (int i = 0; i < grants.size(); i++) {
            grants.get(i).setType(types.get(i));
            grants.get(i).setId(10L + i);
            grants.get(i).setRoleId(source.getId());
        }
        when(permissions.selectList(any())).thenReturn(grants);

        var response = roleMvc().perform(post("/role/copy").contentType(MediaType.APPLICATION_JSON).content("""
                {"roleId":"2","code":"copy","name":"新角色"}
                """)).andExpect(status().isOk()).andReturn().getResponse();
        long roleId = json.readTree(response.getContentAsByteArray()).path("data").asLong();
        assertTrue(roleId > 0 && roleId != source.getId());

        verify(roles).insert(argThat((Role role) -> roleId == role.getId() && "copy".equals(role.getCode())
                && "新角色".equals(role.getName()) && "ENABLED".equals(role.getStatus()) && "NORMAL".equals(role.getType())
                && "".equals(role.getRemark()) && source.getPermissionFlag().equals(role.getPermissionFlag())));
        verify(permissions).insertBatchSomeColumn(argThat(rows -> rows.size() == grants.size()
                && grants.stream().allMatch(grant -> rows.stream().anyMatch(row ->
                        roleId == row.getRoleId() && row.getId() != null && !row.getId().equals(grant.getId())
                                && grant.getType().equals(row.getType())
                                && grant.getRelevanceId().equals(row.getRelevanceId())
                                && grant.getRelevanceValue().equals(row.getRelevanceValue())))));
        verify(commonLogs).addLog(argThat(entry -> "ROLE".equals(entry.getBusinessType())
                && Long.toString(roleId).equals(entry.getBusinessKey()) && "复制".equals(entry.getOperationType())
                && entry.getContent().contains("原角色") && entry.getContent().contains("source")
                && !entry.getContent().contains("orders:edit") && !entry.getContent().contains("ALL_MENU_PERMISSION")));
        verifyNoMoreInteractions(commonLogs);
    }

    @Test
    void rejectsInvalidCopyParametersBeforeCreatingTheRole() throws Exception {
        var mvc = roleMvc();
        for (String body : List.of(
                """
                {"code":"copy","name":"新角色"}
                """,
                """
                {"roleId":"2","code":" ","name":"新角色"}
                """,
                """
                {"roleId":"2","code":"copy","name":"%s"}
                """.formatted("长".repeat(101)),
                """
                {"roleId":"2","code":"copy","name":"新角色","remark":"%s"}
                """.formatted("长".repeat(1001)))) {
            mvc.perform(post("/role/copy").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(roles, permissions, commonLogs);
    }

    @Test
    void acceptsThreePostRequestsAndPreservesSnowflakeIds() throws Exception {
        var mvc = roleMvc();
        long menuId = 1981234567890123457L;
        when(menus.selectList(any())).thenReturn(List.of(menu(menuId, "订单管理", "orders:edit")));

        mvc.perform(post("/role/authorize-menu").contentType(MediaType.APPLICATION_JSON).content("""
                {"roleId":"1981234567890123456","menuIds":["1981234567890123457"],"buttonCodes":["orders:edit"]}
                """)).andExpect(status().isOk());
        mvc.perform(post("/role/authorize-field").contentType(MediaType.APPLICATION_JSON).content("""
                {"roleId":"1981234567890123456","fieldCodes":["%s"]}
                """.formatted(FieldPermissionConstant.USER_PHONE))).andExpect(status().isOk());
        mvc.perform(post("/role/authorize-data-user").contentType(MediaType.APPLICATION_JSON).content("""
                {"roleId":"1981234567890123456","userIds":["1981234567890123458"],"operation":"GRANT"}
                """)).andExpect(status().isOk());
        mvc.perform(put("/role/authorize").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isNotFound());

        verify(permissions).insertBatchSomeColumn(argThat(rows -> rows.stream()
                .anyMatch(row -> Long.valueOf(menuId).equals(row.getRelevanceId()))));
        verify(permissions).insertBatchSomeColumn(argThat(rows -> rows.stream()
                .anyMatch(row -> Long.valueOf(1981234567890123458L).equals(row.getRelevanceId()))));
        verify(commonLogs, times(3)).addLog(argThat(entry -> "1981234567890123456".equals(entry.getBusinessKey())));
    }

    @Test
    void storesMenuAndButtonGrantsInTheirRespectiveTypes() {
        assertTrue(service.authorizeMenu(menuRequest("""
                {"roleId":2,"menuIds":[8],"buttonCodes":["orders:edit"]}
                """)));
        verify(permissions).insertBatchSomeColumn(argThat(rows -> rows.stream()
                .anyMatch(row -> "BUTTON".equals(row.getType()) && "orders:edit".equals(row.getRelevanceValue()))));
        verify(permissions).insertBatchSomeColumn(argThat(rows -> rows.stream()
                .anyMatch(row -> "MENU".equals(row.getType()) && Long.valueOf(8).equals(row.getRelevanceId()))));
        verify(commonLogs).addLog(argThat(entry -> "ROLE".equals(entry.getBusinessType())
                && "2".equals(entry.getBusinessKey()) && "授权".equals(entry.getOperationType())
                && entry.getContent().contains("授予菜单权限：订单管理")
                && entry.getContent().contains("授予按钮权限：订单管理 / 编辑")
                && !entry.getContent().contains("8") && !entry.getContent().contains("orders:edit")));
    }

    @Test
    void resolvesGrantedAndRevokedButtonsFromTheirRespectiveMenus() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(),
                "role-authorization-test"), Menu.class);
        when(menus.selectList(any())).thenAnswer(invocation -> {
            LambdaQueryWrapper<?> query = invocation.getArgument(0);
            query.getSqlSegment();
            var ids = query.getParamNameValuePairs().values();
            var records = new ArrayList<Menu>();
            if (ids.contains(8L)) {
                records.add(menu(8L, "订单管理", "orders:edit"));
            }
            if (ids.contains(17L)) {
                records.add(menu(17L, "商品管理", "products:edit"));
            }
            return records;
        });
        when(permissions.selectList(any())).thenReturn(
                List.of(permission(17L, "")), List.of(permission(0L, "products:edit")));

        assertTrue(service.authorizeMenu(menuRequest("""
                {"roleId":2,"menuIds":[8],"buttonCodes":["orders:edit"]}
                """)));

        verify(commonLogs).addLog(argThat(entry -> entry.getContent().contains("授予按钮权限：订单管理 / 编辑")
                && entry.getContent().contains("取消按钮权限：商品管理 / 编辑")
                && !entry.getContent().contains("products:edit") && !entry.getContent().contains("17")));
    }

    @Test
    void auditsFieldPermissionChangesWithTheirDisplayNames() {
        assertTrue(service.authorizeField(fieldRequest("""
                {"roleId":2,"fieldCodes":["%s"]}
                """.formatted(FieldPermissionConstant.USER_PHONE))));

        verify(commonLogs).addLog(argThat(entry -> entry.getContent().contains("授予字段权限：手机号码")
                && !entry.getContent().contains(FieldPermissionConstant.USER_PHONE)));
    }

    @Test
    void auditsOnlyChangesToAllPermissionFlagsByName() {
        var dto = new RoleAuthorizeAllDTO();
        dto.setRoleId(2L);
        dto.setPermissionFlag("ALL_MENU_PERMISSION");
        dto.setAuthorized(true);
        var originalRole = new Role();
        originalRole.setId(2L);
        var grantedRole = new Role();
        grantedRole.setId(2L);
        grantedRole.setPermissionFlag(List.of("ALL_MENU_PERMISSION"));
        when(roles.selectById(2L)).thenReturn(originalRole, grantedRole, grantedRole);
        when(roles.updateById(any(Role.class))).thenReturn(1);

        assertTrue(service.authorizeAll(dto));
        assertTrue(service.authorizeAll(dto));
        dto.setAuthorized(false);
        assertTrue(service.authorizeAll(dto));

        var written = inOrder(commonLogs);
        written.verify(commonLogs).addLog(argThat(entry -> "授权".equals(entry.getOperationType())
                && "授予所有菜单权限".equals(entry.getContent())));
        written.verify(commonLogs).addLog(argThat(entry -> "取消授权".equals(entry.getOperationType())
                && "取消所有菜单权限".equals(entry.getContent())));
        verifyNoMoreInteractions(commonLogs);
    }

    @Test
    void failsWhenAuditInsertIsRejected() {
        when(commonLogs.addLog(any())).thenReturn(false);

        assertThrows(PromptException.class, () -> service.authorizeField(fieldRequest("""
                {"roleId":2,"fieldCodes":["%s"]}
                """.formatted(FieldPermissionConstant.USER_PHONE))));

        verify(permissions).insertBatchSomeColumn(any());
        verify(redis, never()).deleteByCacheRegion(CacheRegion.PERMISSION);
    }

    @Test
    void identicalMenuAndButtonGrantsProduceNoAuditLog() {
        when(permissions.selectList(any())).thenReturn(
                List.of(permission(8L, "")), List.of(permission(0L, "orders:edit")));

        assertTrue(service.authorizeMenu(menuRequest("""
                {"roleId":2,"menuIds":[8],"buttonCodes":["orders:edit"]}
                """)));

        verifyNoInteractions(commonLogs);
    }

    @Test
    void rejectsUnknownButtonBeforeReplacingMenus() {
        assertThrows(PromptException.class, () -> service.authorizeMenu(menuRequest("""
                {"roleId":2,"menuIds":[8],"buttonCodes":["*"]}
                """)));
        verifyNoInteractions(permissions);
    }

    @Test
    void clearsMenusAndButtonsWhenBothCollectionsAreEmpty() {
        var assistant = new MapperBuilderAssistant(new MybatisConfiguration(), "role-authorization-test");
        TableInfoHelper.initTableInfo(assistant, RolePermissionRelevance.class);
        when(permissions.selectList(any())).thenReturn(
                List.of(permission(8L, ""), permission(17L, "")),
                List.of(permission(0L, "orders:edit"), permission(0L, "missing:button")));
        var clearedTypes = new ArrayList<>();
        when(permissions.delete(any())).thenAnswer(invocation -> {
            var query = invocation.<LambdaQueryWrapper<?>>getArgument(0);
            query.getSqlSegment();
            clearedTypes.addAll(query.getParamNameValuePairs().values());
            return 2;
        });

        assertTrue(service.authorizeMenu(menuRequest("""
                {"roleId":2,"menuIds":[],"buttonCodes":[]}
                """)));
        assertTrue(clearedTypes.containsAll(List.of("MENU", "BUTTON")));
        verify(permissions, never()).insertBatchSomeColumn(any());
        verify(commonLogs).addLog(argThat(entry -> {
            var content = entry.getContent();
            return "取消授权".equals(entry.getOperationType())
                    && content.contains("取消菜单权限：订单管理、名称已不可用（1项）")
                    && content.contains("取消按钮权限：订单管理 / 编辑、名称已不可用（1项）")
                    && !content.contains("missing:button") && !content.contains("17");
        }));
    }

    @Test
    void rejectsIncompleteMenuPayloadBeforeReplacingAnything() throws Exception {
        var mvc = roleMvc();
        mvc.perform(post("/role/authorize-menu").contentType(MediaType.APPLICATION_JSON).content("""
                {"roleId":"2","menuIds":["8"]}
                """)).andExpect(status().isBadRequest());
        mvc.perform(post("/role/authorize-menu").contentType(MediaType.APPLICATION_JSON).content("""
                {"roleId":"2","menuIds":["0"],"buttonCodes":[]}
                """)).andExpect(status().isBadRequest());
        verifyNoInteractions(permissions);
    }

    @Test
    void rejectsInvalidOperationNamesAndOrdinals() {
        for (String operation : List.of("\"REPLACE\"", "0", "\"0\"")) {
            assertThrows(JacksonException.class, () -> dataUserRequest("""
                    {"roleId":2,"operation":%s,"userIds":[9]}
                    """.formatted(operation)));
        }
    }

    @Test
    void grantsOnlyMissingUsersAndAllowsRetry() {
        when(permissions.selectList(any())).thenReturn(List.of(), List.of(permission(9L, "")));
        when(users.selectNamesForRoleAudit(List.of(9L))).thenReturn(List.of(user(9L, "王宁")));
        var request = dataUserRequest("""
                {"roleId":2,"operation":"GRANT","userIds":[9,9]}
                """);

        assertTrue(service.authorizeDataUser(request));
        assertTrue(service.authorizeDataUser(request));
        verify(permissions, never()).delete(any());
        verify(permissions).insertBatchSomeColumn(argThat(rows -> rows.size() == 1 && rows.stream()
                .allMatch(row -> "DATA_USER".equals(row.getType()) && Long.valueOf(9).equals(row.getRelevanceId()))));
        verify(commonLogs).addLog(argThat(entry -> "授予用户数据权限：王宁".equals(entry.getContent())));
        verifyNoMoreInteractions(commonLogs);
    }

    @Test
    void revokesOnlySelectedUsersAndAllowsRetry() {
        var assistant = new MapperBuilderAssistant(new MybatisConfiguration(), "role-authorization-test");
        TableInfoHelper.initTableInfo(assistant, RolePermissionRelevance.class);
        when(permissions.selectList(any())).thenReturn(List.of(permission(9L, "")), List.of());
        var deletedUser = user(9L, "陈敏");
        deletedUser.setDeleteFlag(9L);
        when(users.selectNamesForRoleAudit(List.of(9L))).thenReturn(List.of(deletedUser));
        var affectedRows = List.of(1, 0).iterator();
        when(permissions.delete(any())).thenAnswer(invocation -> {
            var query = invocation.<LambdaQueryWrapper<?>>getArgument(0);
            assertTrue(query.getSqlSegment().contains("relevance_id IN"));
            assertTrue(query.getParamNameValuePairs().values().containsAll(List.of(2L, "DATA_USER", 9L, 19L)));
            return affectedRows.next();
        });
        var request = dataUserRequest("""
                {"roleId":2,"operation":"REVOKE","userIds":[9,19]}
                """);

        assertTrue(service.authorizeDataUser(request));
        assertTrue(service.authorizeDataUser(request));
        verify(permissions, times(2)).delete(any());
        verify(permissions, never()).insertBatchSomeColumn(any());
        verify(commonLogs).addLog(argThat(entry -> "取消授权".equals(entry.getOperationType())
                && "取消用户数据权限：陈敏".equals(entry.getContent())));
        verifyNoMoreInteractions(commonLogs);
    }

    @Test
    void rejectsMissingOperationAndEmptySelection() throws Exception {
        var mvc = roleMvc();
        for (String body : List.of(
                """
                {"roleId":"2","userIds":["9"]}
                """,
                """
                {"roleId":"2","operation":"REVOKE","userIds":[]}
                """,
                """
                {"roleId":"2","operation":"GRANT","userIds":[null]}
                """)) {
            mvc.perform(post("/role/authorize-data-user").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(permissions);
    }

    @Test
    void clearsFieldGrantsOnlyWhenAnEmptyCollectionIsExplicit() throws Exception {
        var mvc = roleMvc();
        mvc.perform(post("/role/authorize-field").contentType(MediaType.APPLICATION_JSON).content("""
                {"roleId":"2"}
                """)).andExpect(status().isBadRequest());
        verifyNoInteractions(permissions);
        when(permissions.selectList(any())).thenReturn(List.of(permission(0L, FieldPermissionConstant.USER_PHONE)));

        mvc.perform(post("/role/authorize-field").contentType(MediaType.APPLICATION_JSON).content("""
                {"roleId":"2","fieldCodes":[]}
                """)).andExpect(status().isOk());
        verify(permissions).delete(any());
        verify(permissions, never()).insertBatchSomeColumn(any());
        verify(commonLogs).addLog(argThat(entry -> "取消字段权限：手机号码".equals(entry.getContent())));
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
        when(converter.toUserPermissionDTO(user)).thenAnswer(invocation -> new UserPermissionDTO());

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

    private MockMvc roleMvc() {
        var controller = new RoleController();
        ReflectionTestUtils.setField(controller, "roleService", service);
        return MockMvcBuilders.standaloneSetup(controller).build();
    }

    private RoleAuthorizeMenuDTO menuRequest(String body) {
        return json.readValue(body, RoleAuthorizeMenuDTO.class);
    }

    private RoleAuthorizeFieldDTO fieldRequest(String body) {
        return json.readValue(body, RoleAuthorizeFieldDTO.class);
    }

    private RoleAuthorizeDataUserDTO dataUserRequest(String body) {
        return json.readValue(body, RoleAuthorizeDataUserDTO.class);
    }

    private static Menu menu(long id, String name, String buttonCode) {
        var button = new MenuButton();
        button.setCode(buttonCode);
        button.setLabel("编辑");
        var menu = new Menu();
        menu.setId(id);
        menu.setMenuName(name);
        menu.setButton(List.of(button));
        return menu;
    }

    private static RolePermissionRelevance permission(long id, String value) {
        var permission = new RolePermissionRelevance();
        permission.setRelevanceId(id);
        permission.setRelevanceValue(value);
        return permission;
    }

    private static User user(long id, String name) {
        var user = new User();
        user.setId(id);
        user.setName(name);
        return user;
    }
}

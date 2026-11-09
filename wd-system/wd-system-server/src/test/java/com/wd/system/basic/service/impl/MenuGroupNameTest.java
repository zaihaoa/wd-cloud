package com.wd.system.basic.service.impl;

import com.wd.common.core.exception.PromptException;
import com.wd.common.redis.util.RedisHelper;
import com.wd.system.basic.domain.dto.MenuAddDTO;
import com.wd.system.basic.domain.dto.MenuUpdateDTO;
import com.wd.system.basic.domain.entity.Menu;
import com.wd.system.basic.mapper.MenuMapper;
import com.wd.system.basic.service.CommonLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MenuGroupNameTest {
    private final MenuServiceImpl service = new MenuServiceImpl();
    private final MenuMapper mapper = mock(MenuMapper.class);
    private final JsonMapper json = JsonMapper.builder().build();
    private final Menu menu = new Menu();
    private final Menu parent = new Menu();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "baseMapper", mapper);
        ReflectionTestUtils.setField(service, "redisHelper", mock(RedisHelper.class));
        ReflectionTestUtils.setField(service, "commonLogService", mock(CommonLogService.class));
        menu.setId(42L);
        menu.setParentId(20L);
        menu.setMenuGroupName("基础");
        parent.setId(20L);
        parent.setParentId(0L);
        parent.setMenuType("DIRECTORY");
        when(mapper.selectOne(any())).thenReturn(menu, parent);
        when(mapper.selectById(20L)).thenReturn(parent);
        when(mapper.alwaysUpdateSomeColumnById(menu)).thenReturn(1);
    }

    @Test
    void trimsGroupNameOnCreate() {
        when(mapper.selectOne(any())).thenReturn(parent);
        var request = json.readValue("""
                {"parentId":20,"menuGroupName":"  基础  "}
                """, MenuAddDTO.class);

        service.addMenu(request);

        var inserted = ArgumentCaptor.forClass(Menu.class);
        verify(mapper).insert(inserted.capture());
        assertEquals("基础", inserted.getValue().getMenuGroupName());
    }

    @Test
    void keepsExistingGroupWhenLegacyUpdateOmitsField() {
        var request = json.readValue("""
                {"id":42,"parentId":20}
                """, MenuUpdateDTO.class);

        service.updateMenu(request);

        assertEquals("基础", menu.getMenuGroupName());
    }

    @Test
    void clearsGroupWhenUpdateExplicitlySendsNull() {
        var request = json.readValue("""
                {"id":42,"parentId":20,"menuGroupName":null}
                """, MenuUpdateDTO.class);

        service.updateMenu(request);

        assertNull(menu.getMenuGroupName());
    }

    @Test
    void rejectsGroupOnDeeperMenu() {
        parent.setParentId(10L);
        var request = json.readValue("""
                {"id":42,"parentId":20,"menuGroupName":"基础"}
                """, MenuUpdateDTO.class);

        assertThrows(PromptException.class, () -> service.updateMenu(request));
    }

    @Test
    void rejectsConvertingDirectoryWithGroupedChildren() {
        when(mapper.selectOne(any())).thenReturn(parent);
        when(mapper.selectList(any())).thenReturn(List.of(menu));
        var request = json.readValue("""
                {"id":20,"parentId":0,"menuType":"MENU"}
                """, MenuUpdateDTO.class);

        assertThrows(PromptException.class, () -> service.updateMenu(request));
    }
}

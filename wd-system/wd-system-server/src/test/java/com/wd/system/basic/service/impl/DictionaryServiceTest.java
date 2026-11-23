package com.wd.system.basic.service.impl;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.wd.common.core.exception.PromptException;
import com.wd.common.core.model.IdDTO;
import com.wd.common.core.model.IdsDTO;
import com.wd.system.basic.controller.DictionaryController;
import com.wd.system.basic.domain.dto.*;
import com.wd.system.basic.domain.entity.Dictionary;
import com.wd.system.basic.domain.entity.DictionaryItem;
import com.wd.system.basic.mapper.DictionaryItemMapper;
import com.wd.system.basic.mapper.DictionaryMapper;
import com.wd.system.basic.service.CommonLogService;
import com.wd.system.basic.service.DictionaryService;
import com.wd.system.basic.service.UserService;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DictionaryServiceTest {

    private final DictionaryMapper dictionaries = mock(DictionaryMapper.class);
    private final DictionaryItemMapper items = mock(DictionaryItemMapper.class);
    private final CommonLogService logs = mock(CommonLogService.class);
    private final DictionaryServiceImpl service = new DictionaryServiceImpl();

    @BeforeEach
    void registerEntityMappings() {
        var assistant = new MapperBuilderAssistant(new MybatisConfiguration(), "dictionary-service-test");
        TableInfoHelper.initTableInfo(assistant, Dictionary.class);
        TableInfoHelper.initTableInfo(assistant, DictionaryItem.class);
        ReflectionTestUtils.setField(service, "baseMapper", dictionaries);
        ReflectionTestUtils.setField(service, "dictionaryItemMapper", items);
        ReflectionTestUtils.setField(service, "commonLogService", logs);
    }

    @Test
    void acceptsAnIdListForDeletingDictionaryItems() throws Exception {
        var controller = new DictionaryController();
        var controllerService = mock(DictionaryService.class);
        ReflectionTestUtils.setField(controller, "dictionaryService", controllerService);
        var mvc = MockMvcBuilders.standaloneSetup(controller).build();

        mvc.perform(post("/dictionary/item-delete").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"ids":[73,74]}
                        """)).andExpect(status().isOk());
        mvc.perform(post("/dictionary/item-delete").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"ids":[]}
                        """)).andExpect(status().isBadRequest());
        verify(controllerService).deleteDictionaryItemsByIds(new IdsDTO(List.of(73L, 74L)));
    }

    @Test
    void returnsStatusAndAuditUserNamesOnTheItemPage() {
        var users = mock(UserService.class);
        ReflectionTestUtils.setField(service, "userService", users);
        when(users.getUserIdNameMap()).thenReturn(Map.of(1L, "张三", 2L, "李四"));

        var query = new DictionaryItemPageParamDTO();
        var item = new DictionaryItemPageDTO();
        item.setStatus("ENABLED");
        item.setCreateUserId(1L);
        item.setUpdateUserId(2L);
        when(items.pageCount(query)).thenReturn(1);
        when(items.pageList(query)).thenReturn(List.of(item));

        var page = service.itemPageList(query);

        assertEquals(1, page.getTotal());
        var result = page.getRecords().getFirst();
        assertEquals("启用", result.getStatusDesc());
        assertEquals("张三", result.getCreateUserName());
        assertEquals("李四", result.getUpdateUserName());
    }

    @Test
    void doesNotDeleteDictionaryWithAnActiveItem() {
        when(dictionaries.selectById(42L)).thenReturn(dictionary());
        when(items.selectOne(any())).thenReturn(new DictionaryItem());

        assertThrows(PromptException.class, () -> service.deleteDictionaryById(new IdDTO(42L)));

        var order = inOrder(dictionaries, items);
        order.verify(dictionaries).selectById(42L);
        order.verify(items).selectOne(argThat(query -> query.getSqlSegment().endsWith("LIMIT 1")));
        verifyNoMoreInteractions(dictionaries, items);
        verifyNoInteractions(logs);
    }

    @Test
    void rejectsNewItemWhenParentIsMissingOrDeleted() {
        assertThrows(PromptException.class, () -> service.addDictionaryItem(itemRequest()));

        verifyNoInteractions(items, logs);
    }

    @Test
    void usesTheStoredDictionaryCodeWhenAddingAnItem() {
        when(dictionaries.selectOne(any())).thenReturn(dictionary());
        when(items.insert(any(DictionaryItem.class))).thenAnswer(call -> {
            call.<DictionaryItem>getArgument(0).setId(73L);
            return 1;
        });
        when(logs.addLog(any())).thenReturn(true);

        assertEquals(73L, service.addDictionaryItem(itemRequest()));

        var order = inOrder(dictionaries, items, logs);
        order.verify(dictionaries).selectOne(argThat(query -> query.getSqlSegment().startsWith("(code = ")
                && !query.getSqlSegment().contains("FOR UPDATE")));
        order.verify(items).insert(argThat((DictionaryItem item) -> "Colors".equals(item.getDictionaryCode())
                && "red".equals(item.getItemCode()) && item.getSortNumber() == 0));
        order.verify(logs).addLog(argThat(log -> "DICTIONARY_ITEM".equals(log.getBusinessType())
                && "73".equals(log.getBusinessKey())));
        verifyNoMoreInteractions(dictionaries);
    }

    @Test
    void rejectsMissingOrBlankRequiredUpdateFields() throws Exception {
        var controller = new DictionaryController();
        var controllerService = mock(DictionaryService.class);
        ReflectionTestUtils.setField(controller, "dictionaryService", controllerService);
        var mvc = MockMvcBuilders.standaloneSetup(controller).build();

        mvc.perform(post("/dictionary/update").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"id":42,"status":"ENABLED"}
                        """)).andExpect(status().isBadRequest());
        mvc.perform(post("/dictionary/update").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"id":42,"desc":"颜色","status":""}
                        """)).andExpect(status().isBadRequest());
        mvc.perform(post("/dictionary/item-update").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"id":73,"itemCode":null,"status":"ENABLED"}
                        """)).andExpect(status().isBadRequest());
        mvc.perform(post("/dictionary/item-update").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"id":73,"itemCode":"red"}
                        """)).andExpect(status().isBadRequest());
        verifyNoInteractions(controllerService);
    }

    @Test
    void clearsOmittedDictionaryRemarkAndWritesRequiredValues() {
        var dictionary = dictionary();
        dictionary.setDesc("描述");
        dictionary.setStatus("ENABLED");
        dictionary.setRemark("备注");
        when(dictionaries.selectById(42L)).thenReturn(dictionary);
        when(dictionaries.updateById(any(Dictionary.class))).thenReturn(1);
        when(logs.addLog(any())).thenReturn(true);

        var request = new DictionaryUpdateDTO();
        request.setId(42L);
        request.setDesc("新描述");
        request.setStatus("DISABLED");
        assertTrue(service.updateDictionary(request));

        verify(dictionaries).updateById(argThat((Dictionary updated) -> "Colors".equals(updated.getCode())
                && "新描述".equals(updated.getDesc()) && "DISABLED".equals(updated.getStatus())
                && "".equals(updated.getRemark())));
        verify(logs).addLog(argThat(log -> "修改字典字段：描述、状态、备注".equals(log.getContent())));
    }

    @Test
    void resetsOmittedItemFieldsAndWritesProvidedValues() {
        var item = new DictionaryItem();
        item.setId(73L);
        item.setDictionaryCode("Colors");
        item.setItemCode("red");
        item.setItemDesc("原描述");
        item.setSortNumber(7);
        item.setStatus("ENABLED");
        item.setRemark("原备注");
        when(items.selectById(73L)).thenReturn(item);
        when(items.updateById(any(DictionaryItem.class))).thenReturn(1);
        when(logs.addLog(any())).thenReturn(true);

        var request = new DictionaryItemUpdateDTO();
        request.setId(73L);
        request.setItemCode("blue");
        request.setStatus("DISABLED");
        assertTrue(service.updateDictionaryItem(request));

        verify(items).updateById(argThat((DictionaryItem updated) -> "Colors".equals(updated.getDictionaryCode())
                && "blue".equals(updated.getItemCode()) && "".equals(updated.getItemDesc())
                && Integer.valueOf(0).equals(updated.getSortNumber()) && "DISABLED".equals(updated.getStatus())
                && "".equals(updated.getRemark())));
    }

    private Dictionary dictionary() {
        var dictionary = new Dictionary();
        dictionary.setId(42L);
        dictionary.setCode("Colors");
        return dictionary;
    }

    private DictionaryItemAddDTO itemRequest() {
        var item = new DictionaryItemAddDTO();
        item.setDictionaryCode("colors");
        item.setItemCode("red");
        item.setStatus("ENABLED");
        return item;
    }
}

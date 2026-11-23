package com.wd.system.basic.service.impl;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.wd.common.core.exception.PromptException;
import com.wd.common.redis.CacheRegion;
import com.wd.common.redis.util.RedisHelper;
import com.wd.system.basic.controller.GlobalConfigController;
import com.wd.system.basic.domain.dto.GlobalConfigAddDTO;
import com.wd.system.basic.domain.dto.GlobalConfigPageParamDTO;
import com.wd.system.basic.domain.dto.GlobalConfigUpdateDTO;
import com.wd.system.basic.domain.entity.GlobalConfig;
import com.wd.system.basic.mapper.GlobalConfigMapper;
import com.wd.system.basic.service.CommonLogService;
import com.wd.system.basic.service.GlobalConfigService;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalConfigServiceTest {

    private final GlobalConfigServiceImpl service = new GlobalConfigServiceImpl();
    private final GlobalConfigMapper mapper = mock(GlobalConfigMapper.class);
    private final CommonLogService commonLog = mock(CommonLogService.class);
    private final RedisHelper redis = mock(RedisHelper.class);

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(),
                "global-config-service-test"), GlobalConfig.class);
        ReflectionTestUtils.setField(service, "baseMapper", mapper);
        ReflectionTestUtils.setField(service, "commonLogService", commonLog);
        ReflectionTestUtils.setField(service, "redisHelper", redis);
    }

    @Test
    void requiresCodeOnAddAndIdAndCodeOnEdit() throws Exception {
        var controller = new GlobalConfigController();
        var controllerService = mock(GlobalConfigService.class);
        ReflectionTestUtils.setField(controller, "globalConfigService", controllerService);
        var mvc = MockMvcBuilders.standaloneSetup(controller).build();

        mvc.perform(post("/global-config/add").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/global-config/add").contentType(MediaType.APPLICATION_JSON).content("""
                {"code":" "}
                """)).andExpect(status().isBadRequest());
        mvc.perform(put("/global-config/update").contentType(MediaType.APPLICATION_JSON).content("""
                {"code":"feature"}
                """)).andExpect(status().isBadRequest());
        mvc.perform(put("/global-config/update").contentType(MediaType.APPLICATION_JSON).content("""
                {"id":42,"code":" "}
                """)).andExpect(status().isBadRequest());
        verifyNoInteractions(controllerService);
    }

    @Test
    void appliesEditAndPreservesOmittedFieldsAndType() {
        var config = new GlobalConfig();
        config.setId(42L);
        config.setCode("feature");
        config.setValue("old-value");
        config.setRemark("old-remark");
        config.setSortNumber(7);
        config.setType("SYSTEM");
        when(mapper.selectById(42L)).thenReturn(config);
        when(mapper.updateById(any(GlobalConfig.class))).thenReturn(1);
        when(commonLog.addLog(any())).thenReturn(true);

        var request = new GlobalConfigUpdateDTO();
        request.setId(42L);
        request.setValue("new-value");
        request.setRemark("");

        TransactionSynchronizationManager.initSynchronization();
        try {
            assertTrue(service.updateGlobalConfig(request));
            verify(mapper).updateById(argThat((GlobalConfig updated) -> "feature".equals(updated.getCode())
                    && "new-value".equals(updated.getValue()) && "".equals(updated.getRemark())
                    && Integer.valueOf(7).equals(updated.getSortNumber()) && "SYSTEM".equals(updated.getType())));
            verify(commonLog).addLog(argThat(entry -> "GLOBAL_CONFIG".equals(entry.getBusinessType())
                    && "42".equals(entry.getBusinessKey()) && "编辑".equals(entry.getOperationType())
                    && "修改全局配置字段：值、备注".equals(entry.getContent())));
            verifyNoInteractions(redis);
            TransactionSynchronizationManager.getSynchronizations().getFirst().afterCommit();
            verify(redis).deleteByCacheRegion(CacheRegion.GLOBAL_CONFIG);
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void rejectsTheChangeAndDoesNotScheduleCacheEvictionWhenAuditFails() {
        when(mapper.insert(any(GlobalConfig.class))).thenReturn(1);
        when(commonLog.addLog(any())).thenReturn(false);
        var request = new GlobalConfigAddDTO();
        request.setCode("feature");

        TransactionSynchronizationManager.initSynchronization();
        try {
            assertThrows(PromptException.class, () -> service.addGlobalConfig(request));
            assertTrue(TransactionSynchronizationManager.getSynchronizations().isEmpty());
            verifyNoInteractions(redis);
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void cachesAnEmptyConfigurationValue() {
        var config = new GlobalConfig();
        config.setValue("");
        when(mapper.selectOne(any())).thenReturn(config);

        assertEquals("", service.getValueByCode("feature"));
        verify(redis).setEx(CacheRegion.GLOBAL_CONFIG, "feature", "", 10, TimeUnit.MINUTES);
    }

    @Test
    void readsTheCodeKeyFromCacheEvenDuringTransactions() {
        when(redis.get(CacheRegion.GLOBAL_CONFIG, "feature", String.class)).thenReturn("cached-value");

        TransactionSynchronizationManager.setActualTransactionActive(true);
        try {
            assertEquals("cached-value", service.getValueByCode("feature"));
            verifyNoInteractions(mapper);
        } finally {
            TransactionSynchronizationManager.setActualTransactionActive(false);
        }
    }

    @Test
    void excludesLogicalDeletesFromCountAndPage() throws IOException {
        var configuration = new Configuration();
        String path = "mapper/basic/GlobalConfigMapper.xml";
        try (var stream = Resources.getResourceAsStream(path)) {
            new XMLMapperBuilder(stream, configuration, path, configuration.getSqlFragments()).parse();
        }
        var page = new GlobalConfigPageParamDTO();
        page.setCode("feature");
        page.setSort("sortNumber");
        page.setOrder("desc");
        var parameters = Map.of("param", page);
        String mapperName = GlobalConfigMapper.class.getName();
        String countSql = configuration.getMappedStatement(mapperName + ".pageCount")
                .getBoundSql(parameters).getSql().replaceAll("\\s+", " ");
        String pageSql = configuration.getMappedStatement(mapperName + ".pageList")
                .getBoundSql(parameters).getSql().replaceAll("\\s+", " ");

        assertTrue(countSql.contains("WHERE `delete_flag` = 0 and `code` like"), countSql);
        assertTrue(pageSql.contains("WHERE `delete_flag` = 0 and `code` like"), pageSql);
        assertTrue(pageSql.contains("ORDER BY `sort_number` DESC , `id` ASC"), pageSql);
    }
}

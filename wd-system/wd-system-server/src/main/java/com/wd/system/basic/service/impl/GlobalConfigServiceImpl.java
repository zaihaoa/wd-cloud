package com.wd.system.basic.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.wd.common.core.constants.CommonConstant;
import com.wd.common.core.model.IdDTO;
import com.wd.common.core.model.PageInfo;
import com.wd.common.core.objectcompare.ObjectCompareUtil;
import com.wd.common.core.util.AssertUtil;
import com.wd.common.core.util.CodeDescEnumUtil;
import com.wd.common.core.util.I18nUtil;
import com.wd.common.mybatisplus.PageUtil;
import com.wd.common.redis.CacheRegion;
import com.wd.common.redis.util.RedisHelper;
import com.wd.system.basic.convert.GlobalConfigConverter;
import com.wd.system.basic.domain.dto.*;
import com.wd.system.basic.domain.entity.GlobalConfig;
import com.wd.system.basic.domain.enums.CommonLogBusinessTypeEnum;
import com.wd.system.basic.domain.enums.GlobalConfigTypeEnum;
import com.wd.system.basic.domain.vo.GlobalConfigDetailVO;
import com.wd.system.basic.domain.vo.GlobalConfigPageVO;
import com.wd.system.basic.mapper.GlobalConfigMapper;
import com.wd.system.basic.service.CommonLogService;
import com.wd.system.basic.service.GlobalConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 全局配置 服务实现类
 * </p>
 *
 * @author huangwenda
 * @since 2025-10-11
 */
@Slf4j
@Service
public class GlobalConfigServiceImpl extends ServiceImpl<GlobalConfigMapper, GlobalConfig> implements GlobalConfigService {

    @Resource
    private GlobalConfigConverter globalConfigConverter;

    @Resource
    private CommonLogService commonLogService;

    @Resource
    private RedisHelper redisHelper;

    @Override
    public PageInfo<GlobalConfigPageVO> pageList(GlobalConfigPageParamDTO pageParam) {

        int count = baseMapper.pageCount(pageParam);
        if (count <= 0) {
            return PageUtil.buildPageInfo(pageParam);
        }

        List<GlobalConfigPageDTO> dbRecords = baseMapper.pageList(pageParam);

        List<GlobalConfigPageVO> voRecords = convertGlobalConfigPageVOList(dbRecords);

        return PageUtil.buildPageInfo(pageParam, count, voRecords);
    }

    private List<GlobalConfigPageVO> convertGlobalConfigPageVOList(List<GlobalConfigPageDTO> dbRecords) {
        return dbRecords
                .stream()
                .map(dbRecord -> {
                    GlobalConfigPageVO vo = globalConfigConverter.convertGlobalConfigPageVO(dbRecord);
                    vo.setTypeDesc(CodeDescEnumUtil.getDescByCode(GlobalConfigTypeEnum.class, dbRecord.getType()));

                    return vo;
                })
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public long addGlobalConfig(GlobalConfigAddDTO globalConfigAddParam) {
        log.info("添加全局配置,参数:{}", JSON.toJSONString(globalConfigAddParam));

        String code = globalConfigAddParam.getCode();
        checkCodeAvailable(code);

        GlobalConfig globalConfig = new GlobalConfig();
        long id = IdWorker.getId();
        globalConfig.setId(id);
        globalConfig.setCode(code);
        globalConfig.setValue(Objects.requireNonNullElse(globalConfigAddParam.getValue(), ""));
        globalConfig.setRemark(Objects.requireNonNullElse(globalConfigAddParam.getRemark(), ""));
        globalConfig.setType(GlobalConfigTypeEnum.NORMAL.getCode());
        globalConfig.setSortNumber(Objects.requireNonNullElse(globalConfigAddParam.getSortNumber(), CommonConstant.ZERO_INTEGER));
        globalConfig.setDeleteFlag(CommonConstant.NOT_DELETE_FLAG);

        AssertUtil.isTrue(baseMapper.insert(globalConfig) > 0, "新增全局配置失败");

        // 记录日志
        addLog(id, "新增", "新增全局配置：" + code);

        // 清除缓存
        clearGlocalConfigCacheAfterCommit();

        return id;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateGlobalConfig(GlobalConfigUpdateDTO globalConfigUpdateParam) {
        log.info("修改全局配置,参数:{}", JSON.toJSONString(globalConfigUpdateParam));

        Long id = globalConfigUpdateParam.getId();
        GlobalConfig record = getGlobalConfigForUpdate(id);

        String compareLog = ObjectCompareUtil.compareLog(record, globalConfigUpdateParam);

        record.setValue(Optional.ofNullable(globalConfigUpdateParam.getValue()).orElse(""));
        record.setRemark(Optional.ofNullable(globalConfigUpdateParam.getRemark()).orElse(""));
        record.setSortNumber(Optional.ofNullable(globalConfigUpdateParam.getSortNumber()).orElse(CommonConstant.ZERO_INTEGER));

        baseMapper.updateById(record);

        if (StringUtils.isNotBlank(compareLog)) {
            addLog(id, "编辑", compareLog);
        }

        // 清除缓存
        clearGlocalConfigCacheAfterCommit();

        return true;
    }

    @Override
    public GlobalConfigDetailVO detail(Long id) {
        GlobalConfig record = getGlobalConfigById(id);
        if (record == null) {
            return null;
        }

        GlobalConfigDetailVO vo = globalConfigConverter.convertGlobalConfigDetailVO(record);
        vo.setTypeDesc(CodeDescEnumUtil.getDescByCode(GlobalConfigTypeEnum.class, record.getType()));

        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteGlobalConfigById(IdDTO idParam) {
        log.info("删除全局配置,参数:{}", JSON.toJSONString(idParam));

        Long id = idParam.getId();
        GlobalConfig record = getGlobalConfigForUpdate(id);
        AssertUtil.isTrue(!GlobalConfigTypeEnum.SYSTEM.getCode().equals(record.getType()), () -> I18nUtil.message("glocal-config.type.not.delete"));

        boolean success = baseMapper.update(Wrappers.<GlobalConfig>lambdaUpdate()
                .set(GlobalConfig::getDeleteFlag, id)
                .eq(GlobalConfig::getId, id)) > 0;

        if (success) {
            addLog(id, "删除", "删除全局配置：" + record.getCode());
        }

        // 清除缓存
        clearGlocalConfigCacheAfterCommit();

        return success;
    }

    @Override
    public GlobalConfig getGlobalConfigById(Long id) {
        if (id == null) {
            return null;
        }
        return baseMapper.selectById(id);
    }

    @Override
    public String getValueByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        String cached = redisHelper.get(CacheRegion.GLOBAL_CONFIG, code, String.class);
        if (cached != null) {
            return cached;
        }

        String value = Optional.ofNullable(baseMapper.selectOne(Wrappers.<GlobalConfig>lambdaQuery()
                        .select(GlobalConfig::getValue)
                        .eq(GlobalConfig::getCode, code)))
                .map(GlobalConfig::getValue)
                .orElse(null);

        redisHelper.setEx(CacheRegion.GLOBAL_CONFIG, code, value, 5, TimeUnit.DAYS);

        return value;
    }

    private GlobalConfig getGlobalConfigForUpdate(Long id) {
        AssertUtil.notNull(id, () -> I18nUtil.message("data.not.exist"));
        GlobalConfig record = baseMapper.selectOne(Wrappers.<GlobalConfig>lambdaQuery()
                .eq(GlobalConfig::getId, id)
                .last("FOR UPDATE"));
        AssertUtil.notNull(record, () -> I18nUtil.message("data.not.exist"));
        return record;
    }

    private void checkCodeAvailable(String code) {
        AssertUtil.hasText(code, "全局配置编码不能为空");
        boolean exists = baseMapper.exists(Wrappers.<GlobalConfig>lambdaQuery()
                .eq(GlobalConfig::getCode, code));
        AssertUtil.isTrue(!exists, "全局配置编码已存在");
    }

    private void addLog(Long id, String operationType, String content) {
        CommonLogAddDTO entry = CommonLogAddDTO.builder()
                .businessType(CommonLogBusinessTypeEnum.GLOBAL_CONFIG.getCode())
                .businessKey(String.valueOf(id))
                .operationType(operationType)
                .content(content)
                .build();
        AssertUtil.isTrue(commonLogService.addLog(entry), "写入全局配置操作日志失败");
    }

    private void clearGlocalConfigCacheAfterCommit() {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            clearGlocalConfigCache();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                clearGlocalConfigCache();
            }
        });
    }

    private void clearGlocalConfigCache() {
        redisHelper.deleteByCacheRegion(CacheRegion.GLOBAL_CONFIG);
    }
}

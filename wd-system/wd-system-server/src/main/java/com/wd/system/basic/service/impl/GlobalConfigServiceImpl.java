package com.wd.system.basic.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wd.common.core.constants.CommonConstant;
import com.wd.common.core.model.Id;
import com.wd.common.core.model.PageInfo;
import com.wd.common.core.util.AssertUtil;
import com.wd.common.core.util.CodeDescEnumUtil;
import com.wd.common.core.util.I18nUtil;
import com.wd.common.mybatisplus.PageUtil;
import com.wd.system.basic.domain.entity.GlobalConfig;
import com.wd.system.basic.domain.enums.GlobalConfigTypeEnum;
import com.wd.system.basic.mapper.GlobalConfigMapper;
import com.wd.system.basic.service.GlobalConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wd.system.basic.convert.GlobalConfigConverter;
import com.wd.system.basic.domain.dto.GlobalConfigPageParamDTO;
import com.wd.system.basic.domain.dto.GlobalConfigPageDTO;
import com.wd.system.basic.domain.vo.GlobalConfigPageVO;
import com.wd.system.basic.domain.vo.GlobalConfigDetailVO;
import com.wd.system.basic.domain.dto.GlobalConfigAddDTO;
import com.wd.system.basic.domain.dto.GlobalConfigUpdateDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

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
        log.info("新增全局配置:{}", JSON.toJSONString(globalConfigAddParam));

        GlobalConfig globalConfig = new GlobalConfig();

        globalConfig.setId(IdWorker.getId());
        globalConfig.setCode(globalConfigAddParam.getCode());
        globalConfig.setValue(globalConfigAddParam.getValue());
        globalConfig.setRemark(globalConfigAddParam.getRemark());
        globalConfig.setType(globalConfigAddParam.getType());
        globalConfig.setSortNumber(Optional.ofNullable(globalConfigAddParam.getSortNumber()).orElse(CommonConstant.ZERO_INTEGER));
        globalConfig.setDeleteFlag(CommonConstant.NOT_DELETE_FLAG);
        baseMapper.insert(globalConfig);

        return globalConfig.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateGlobalConfig(GlobalConfigUpdateDTO globalConfigUpdateParam) {
        log.info("更新全局配置:{}", JSON.toJSONString(globalConfigUpdateParam));

        GlobalConfig existRecord = getGlobalConfigByIdAssertExist(globalConfigUpdateParam.getId());

        // todo

        boolean success = baseMapper.updateById(existRecord) > 0;

        return success;
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
    public boolean deleteGlobalConfigById(Id idParam) {
        log.info("删除全局配置:{}", JSON.toJSONString(idParam));
        Long id = idParam.getId();

        // 判断数据是否存在
        GlobalConfig record = getGlobalConfigByIdAssertExist(id);
        AssertUtil.isTrue(!GlobalConfigTypeEnum.SYSTEM.getCode().equals(record.getType()), () -> I18nUtil.message("glocal-config.type.not.delete"));

        boolean success = baseMapper.update(Wrappers.<GlobalConfig>lambdaUpdate()
                .set(GlobalConfig::getDeleteFlag, id)
                .eq(GlobalConfig::getId, id)) > 0;

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
    public GlobalConfig getGlobalConfigByIdAssertExist(Long id) {
        GlobalConfig record = getGlobalConfigById(id);
        AssertUtil.notNull(record, () -> I18nUtil.message("data.not.exist"));
        return record;
    }
}

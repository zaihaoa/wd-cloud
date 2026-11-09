package com.wd.system.basic.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.wd.common.core.context.SystemContext;
import com.wd.common.core.util.CodeDescEnumUtil;
import com.wd.system.basic.domain.dto.UserConfigAddUpdateDTO;
import com.wd.system.basic.domain.dto.UserConfigQueryDTO;
import com.wd.system.basic.domain.entity.UserConfig;
import com.wd.system.basic.domain.enums.UserConfigTypeEnum;
import com.wd.system.basic.domain.vo.UserConfigDetailVO;
import com.wd.system.basic.mapper.UserConfigMapper;
import com.wd.system.basic.service.UserConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户配置 服务实现类
 * </p>
 *
 * @author huangwenda
 * @since 2026-11-09
 */
@Slf4j
@Service
public class UserConfigServiceImpl extends ServiceImpl<UserConfigMapper, UserConfig> implements UserConfigService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserConfigDetailVO addOrUpdate(UserConfigAddUpdateDTO addUpdateDTO) {
        log.info("新增或更新参数:{}", JSON.toJSONString(addUpdateDTO));

        Long userId = SystemContext.getUserIdAssertExist();
        String type = addUpdateDTO.getType();
        String code = addUpdateDTO.getCode();
        String content = addUpdateDTO.getContent();

        UserConfig existRecord = baseMapper.selectOne(Wrappers.<UserConfig>lambdaQuery()
                .eq(UserConfig::getUserId, userId)
                .eq(UserConfig::getType, type)
                .eq(UserConfig::getCode, code));

        if (existRecord == null) {
            // 新增
            UserConfig customColumn = new UserConfig();
            long id = IdWorker.getId();
            customColumn.setId(id);
            customColumn.setUserId(userId);
            customColumn.setType(type);
            customColumn.setCode(code);
            customColumn.setContent(content);
            baseMapper.insert(customColumn);

            return buildUserConfigDetailVO(customColumn);
        } else {
            // 更新
            existRecord.setContent(content);
            baseMapper.updateById(existRecord);

            return buildUserConfigDetailVO(existRecord);
        }
    }

    @Override
    public UserConfigDetailVO detail(UserConfigQueryDTO query) {
        String type = query.getType();
        String code = query.getCode();
        if (StringUtils.isBlank(code)) {
            return null;
        }

        UserConfig existRecord = baseMapper.selectOne(Wrappers.<UserConfig>lambdaQuery()
                .eq(UserConfig::getUserId, SystemContext.getUserIdAssertExist())
                .eq(UserConfig::getType, type)
                .eq(UserConfig::getCode, code));
        if (existRecord == null) {
            return null;
        }

        return buildUserConfigDetailVO(existRecord);
    }

    private UserConfigDetailVO buildUserConfigDetailVO(UserConfig record) {
        if (record == null) {
            return null;
        }

        UserConfigDetailVO vo = new UserConfigDetailVO();
        vo.setId(record.getId());
        vo.setUserId(record.getUserId());
        vo.setType(record.getType());
        vo.setTypeDesc(CodeDescEnumUtil.getDescByCode(UserConfigTypeEnum.class, record.getType()));
        vo.setCode(record.getCode());
        vo.setContent(record.getContent());
        vo.setCreateUserId(record.getCreateUserId());
        vo.setUpdateUserId(record.getUpdateUserId());
        vo.setCreateTime(record.getCreateTime());
        vo.setUpdateTime(record.getUpdateTime());

        return vo;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delete(UserConfigQueryDTO query) {
        log.info("删除参数:{}", JSON.toJSONString(query));

        String type = query.getType();
        String code = query.getCode();
        if (StringUtils.isBlank(type) || StringUtils.isBlank(code)) {
            return false;
        }

        UserConfig existRecord = baseMapper.selectOne(Wrappers.<UserConfig>lambdaQuery()
                .eq(UserConfig::getUserId, SystemContext.getUserIdAssertExist())
                .eq(UserConfig::getType, type)
                .eq(UserConfig::getCode, code));
        log.info("");
        if (existRecord == null) {
            return false;
        }

        int delete = baseMapper.delete(Wrappers.<UserConfig>lambdaQuery()
                .eq(UserConfig::getUserId, SystemContext.getUserIdAssertExist())
                .eq(UserConfig::getType, type)
                .eq(UserConfig::getCode, code));
        return delete > 0;
    }
}

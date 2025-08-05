package com.wd.system.basic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wd.common.core.constants.CommonConstant;
import com.wd.common.core.model.PageInfo;
import com.wd.common.mybatisplus.PageUtil;
import com.wd.system.basic.domain.dto.CommonLogAddDTO;
import com.wd.system.basic.domain.dto.CommonLogPageDTO;
import com.wd.system.basic.domain.dto.CommonLogPageParamDTO;
import com.wd.system.basic.domain.entity.CommonLog;
import com.wd.system.basic.domain.vo.CommonLogPageVO;
import com.wd.system.basic.mapper.CommonLogMapper;
import com.wd.system.basic.service.CommonLogService;
import com.wd.system.basic.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 通用日志 服务实现类
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@Slf4j
@Service
public class CommonLogServiceImpl extends ServiceImpl<CommonLogMapper, CommonLog> implements CommonLogService {

    @Resource
    private UserService userService;

    @Override
    public PageInfo<CommonLogPageVO> pageList(CommonLogPageParamDTO pageParam) {

        // 默认创建时间降序
        pageParam.setSort("createTime");
        pageParam.setOrder(PageUtil.DESC);

        int count = baseMapper.pageCount(pageParam);
        if (count <= 0) {
            return PageUtil.buildPageInfo(pageParam);
        }

        List<CommonLogPageDTO> dbRecords = baseMapper.pageList(pageParam);

        List<CommonLogPageVO> voRecords = convertCommonLogPageVOList(dbRecords);

        return PageUtil.buildPageInfo(pageParam, count, voRecords);
    }

    private List<CommonLogPageVO> convertCommonLogPageVOList(List<CommonLogPageDTO> dbRecords) {

        Set<Long> userIds = Sets.newHashSet();
        userIds.addAll(dbRecords.stream().map(CommonLogPageDTO::getCreateUserId).collect(Collectors.toSet()));
        userIds.addAll(dbRecords.stream().map(CommonLogPageDTO::getUpdateUserId).collect(Collectors.toSet()));

        Map<Long, String> userIdNameMap = userService.getUserIdNameMap(userIds);

        return dbRecords
                .stream()
                .map(dbRecord -> {
                    CommonLogPageVO vo = new CommonLogPageVO();
                    vo.setId(dbRecord.getId());
                    vo.setBusinessType(dbRecord.getBusinessType());
                    vo.setBusinessId(dbRecord.getBusinessId());
                    vo.setOperationType(dbRecord.getOperationType());
                    vo.setContent(dbRecord.getContent());
                    vo.setCreateUserId(dbRecord.getCreateUserId());
                    vo.setUpdateUserId(dbRecord.getUpdateUserId());
                    vo.setCreateUserName(userIdNameMap.get(dbRecord.getCreateUserId()));
                    vo.setUpdateUserName(userIdNameMap.get(dbRecord.getUpdateUserId()));
                    vo.setCreateTime(dbRecord.getCreateTime());
                    vo.setUpdateTime(dbRecord.getUpdateTime());

                    return vo;
                })
                .toList();
    }

    @Override
    public boolean addLog(CommonLogAddDTO commonLogAddParam) {
        return batchAddLog(Collections.singletonList(commonLogAddParam));
    }

    @Override
    public boolean batchAddLog(List<CommonLogAddDTO> commonLogAddParams) {
        if (CollUtil.isEmpty(commonLogAddParams)) {
            log.info("没有日志");
            return false;
        }

        List<CommonLog> addRecords = Lists.newArrayList();

        for (CommonLogAddDTO commonLogAddParam : commonLogAddParams) {

            String content = Optional.ofNullable(commonLogAddParam.getContent()).orElse("");
            // 如果内容超出10000,截取分批插入
            String[] splitContentList = StrUtil.split(content, 10000);
            for (String splitContent : splitContentList) {
                CommonLog commonLog = new CommonLog();
                commonLog.setId(IdWorker.getId());
                commonLog.setBusinessType(Optional.ofNullable(commonLogAddParam.getBusinessType()).orElse(""));
                commonLog.setBusinessId(Optional.ofNullable(commonLogAddParam.getBusinessId()).orElse(CommonConstant.LONG_ZERO));
                commonLog.setOperationType(Optional.ofNullable(commonLogAddParam.getOperationType()).orElse(""));
                commonLog.setContent(splitContent);

                addRecords.add(commonLog);
            }
        }

        return baseMapper.insertBatchSomeColumn(addRecords) > 0;
    }

}

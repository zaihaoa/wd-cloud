package com.wd.system.basic.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.wd.common.core.constants.CommonConstant;
import com.wd.common.core.model.IdDTO;
import com.wd.common.core.model.IdsDTO;
import com.wd.common.core.model.PageInfo;
import com.wd.common.core.objectcompare.ObjectCompareCustom;
import com.wd.common.core.objectcompare.ObjectCompareUtil;
import com.wd.common.core.util.AssertUtil;
import com.wd.common.core.util.CodeDescEnumUtil;
import com.wd.common.mybatisplus.PageUtil;
import com.wd.common.redis.CacheRegion;
import com.wd.common.redis.util.RedisHelper;
import com.wd.system.basic.convert.DictionaryConverter;
import com.wd.system.basic.domain.dto.*;
import com.wd.system.basic.domain.entity.Dictionary;
import com.wd.system.basic.domain.entity.DictionaryItem;
import com.wd.system.basic.domain.enums.CommonLogBusinessTypeEnum;
import com.wd.system.basic.domain.enums.DictionaryStatusEnum;
import com.wd.system.basic.domain.vo.DictionaryDetailVO;
import com.wd.system.basic.domain.vo.DictionaryItemDetailVO;
import com.wd.system.basic.domain.vo.DictionaryItemPageVO;
import com.wd.system.basic.domain.vo.DictionaryPageVO;
import com.wd.system.basic.mapper.DictionaryItemMapper;
import com.wd.system.basic.mapper.DictionaryMapper;
import com.wd.system.basic.service.CommonLogService;
import com.wd.system.basic.service.DictionaryService;
import com.wd.system.basic.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService {

    @Resource
    private DictionaryConverter dictionaryConverter;
    @Resource
    private DictionaryItemMapper dictionaryItemMapper;
    @Resource
    private CommonLogService commonLogService;
    @Resource
    private UserService userService;
    @Resource
    private RedisHelper redisHelper;

    @Override
    public PageInfo<DictionaryPageVO> pageList(DictionaryPageParamDTO pageParam) {
        int count = baseMapper.pageCount(pageParam);
        if (count == 0) {
            return PageUtil.buildPageInfo(pageParam);
        }

        List<DictionaryPageDTO> records = baseMapper.pageList(pageParam);

        Map<Long, String> userIdNameMap = userService.getUserIdNameMap();

        return PageUtil.buildPageInfo(pageParam, count, records, record -> {
            DictionaryPageVO vo = dictionaryConverter.toDictionaryPageVO(record);
            vo.setStatusDesc(CodeDescEnumUtil.getDescByCode(DictionaryStatusEnum.class, vo.getStatus()));
            vo.setCreateUserName(userIdNameMap.get(vo.getCreateUserId()));
            vo.setUpdateUserName(userIdNameMap.get(vo.getUpdateUserId()));
            return vo;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addDictionary(DictionaryAddDTO param) {
        log.info("新增字典,参数:{}", JSON.toJSONString(param));
        String code = param.getCode();
        String desc = param.getDesc();
        AssertUtil.isTrue(!baseMapper.exists(Wrappers.<Dictionary>lambdaQuery().eq(Dictionary::getCode, code)), "字典编码已存在");

        Dictionary record = new Dictionary();
        long id = IdWorker.getId();
        record.setId(id);
        record.setCode(code);
        record.setDesc(desc);
        record.setStatus(param.getStatus());
        record.setRemark(Objects.requireNonNullElse(param.getRemark(), ""));
        record.setDeleteFlag(CommonConstant.NOT_DELETE_FLAG);

        AssertUtil.isTrue(baseMapper.insert(record) == 1, "新增字典失败");

        addDictionaryLog(id, "新增", "新增字典：" + codeDescJoinShow(code, desc));

        return id;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDictionary(DictionaryUpdateDTO param) {
        log.info("更新字典,参数:{}", JSON.toJSONString(param));

        Dictionary record = getRequiredDictionary(param.getId());

        String compareLog = ObjectCompareUtil.compareLog(record, param,
                ObjectCompareCustom.<DictionaryUpdateDTO, String>of(DictionaryUpdateDTO::getStatus,
                        value -> CodeDescEnumUtil.getDescByCode(DictionaryStatusEnum.class, value)));

        baseMapper.update(new Dictionary(), Wrappers.<Dictionary>lambdaUpdate()
                .set(Dictionary::getDesc, param.getDesc())
                .set(Dictionary::getStatus, param.getStatus())
                .set(Dictionary::getRemark, Objects.requireNonNullElse(param.getRemark(), ""))
                .eq(Dictionary::getId, record.getId()));

        if (StringUtils.isNotBlank(compareLog)) {
            addDictionaryLog(record.getId(), "编辑", compareLog);
        }

        // 清除缓存
        clearDictionaryCacheAfterCommit(record.getCode());

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateDictionaryStatus(DictionaryBatchUpdateStatusDTO param) {
        log.info("批量更新字典状态,参数:{}", JSON.toJSONString(param));

        List<Long> ids = validateBatchStatusUpdate(param);
        String status = param.getStatus();
        List<Dictionary> records = baseMapper.selectByIds(ids);
        AssertUtil.isTrue(records.size() == ids.size(), "部分字典不存在或已删除");

        List<Dictionary> changedRecords = records.stream()
                .filter(record -> !status.equals(record.getStatus()))
                .toList();
        if (changedRecords.isEmpty()) {
            return true;
        }

        List<Long> changedIds = changedRecords.stream().map(Dictionary::getId).toList();
        int rows = baseMapper.update(new Dictionary(), Wrappers.<Dictionary>lambdaUpdate()
                .set(Dictionary::getStatus, status)
                .in(Dictionary::getId, changedIds)
                .ne(Dictionary::getStatus, status));
        AssertUtil.isTrue(rows == changedIds.size(), "批量更新字典状态失败");

        String operation = CodeDescEnumUtil.getDescByCode(DictionaryStatusEnum.class, status);
        List<CommonLogAddDTO> logs = changedRecords
                .stream()
                .map(record -> buildDictionaryLog(record.getId(), "修改状态", operation + "字典：" + codeDescJoinShow(record.getCode(), record.getDesc())))
                .toList();
        AssertUtil.isTrue(commonLogService.batchAddLog(logs), "写入字典操作日志失败");

        changedRecords.stream().map(Dictionary::getCode).distinct().forEach(this::clearDictionaryCacheAfterCommit);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictionaryById(IdDTO param) {
        log.info("删除字典,参数:{}", JSON.toJSONString(param));
        Dictionary record = getRequiredDictionary(param.getId());
        String code = record.getCode();

        long itemCount = dictionaryItemMapper.selectCount(Wrappers.<DictionaryItem>lambdaQuery()
                .eq(DictionaryItem::getDictionaryCode, code));
        AssertUtil.isTrue(itemCount == 0, "请先删除该字典下的明细");

        // 传入实体以触发更新人自动填充，更新时间由数据库更新。
        int rows = baseMapper.update(new Dictionary(), Wrappers.<Dictionary>lambdaUpdate()
                .set(Dictionary::getDeleteFlag, record.getId())
                .eq(Dictionary::getId, record.getId()));
        AssertUtil.isTrue(rows == 1, "删除字典失败");

        addDictionaryLog(record.getId(), "删除", "删除字典：" + codeDescJoinShow(record.getCode(), record.getDesc()));

        // 清除缓存
        clearDictionaryCacheAfterCommit(code);
        return true;
    }

    @Override
    public DictionaryDetailVO detail(Long id) {
        Dictionary record = baseMapper.selectById(id);
        if (Objects.isNull(record)) {
            return null;
        }
        DictionaryDetailVO vo = dictionaryConverter.toDictionaryDetailVO(record);
        vo.setStatusDesc(CodeDescEnumUtil.getDescByCode(DictionaryStatusEnum.class, vo.getStatus()));
        return vo;
    }

    @Override
    public PageInfo<DictionaryItemPageVO> itemPageList(DictionaryItemPageParamDTO pageParam) {
        int count = dictionaryItemMapper.pageCount(pageParam);
        if (count == 0) {
            return PageUtil.buildPageInfo(pageParam);
        }

        List<DictionaryItemPageDTO> records = dictionaryItemMapper.pageList(pageParam);
        Map<Long, String> userIdNameMap = userService.getUserIdNameMap();

        return PageUtil.buildPageInfo(pageParam, count, records, record -> {
            DictionaryItemPageVO vo = dictionaryConverter.toDictionaryItemPageVO(record);
            vo.setStatusDesc(CodeDescEnumUtil.getDescByCode(DictionaryStatusEnum.class, vo.getStatus()));
            vo.setCreateUserName(userIdNameMap.get(vo.getCreateUserId()));
            vo.setUpdateUserName(userIdNameMap.get(vo.getUpdateUserId()));
            return vo;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addDictionaryItem(DictionaryItemAddDTO param) {
        log.info("新增字典明细,参数:{}", JSON.toJSONString(param));

        Dictionary dictionary = baseMapper.selectOne(Wrappers.<Dictionary>lambdaQuery()
                .eq(Dictionary::getCode, param.getDictionaryCode()));
        AssertUtil.notNull(dictionary, "所属字典不存在或已删除");

        String dictionaryCode = dictionary.getCode();
        String itemCode = param.getItemCode();
        String itemDesc = param.getItemDesc();

        boolean exists = dictionaryItemMapper.exists(Wrappers.<DictionaryItem>lambdaQuery()
                .eq(DictionaryItem::getDictionaryCode, dictionaryCode)
                .eq(DictionaryItem::getItemCode, itemCode));
        AssertUtil.isTrue(!exists, "同一字典下的明细编码已存在");

        DictionaryItem record = new DictionaryItem();
        long id = IdWorker.getId();
        record.setId(id);
        record.setDictionaryCode(dictionaryCode);
        record.setItemCode(itemCode);
        record.setItemDesc(itemDesc);
        record.setSortNumber(Objects.requireNonNullElse(param.getSortNumber(), 0));
        record.setStatus(param.getStatus());
        record.setRemark(Objects.requireNonNullElse(param.getRemark(), ""));
        record.setDeleteFlag(CommonConstant.NOT_DELETE_FLAG);

        AssertUtil.isTrue(dictionaryItemMapper.insert(record) == 1, "新增字典明细失败");

        addDictionaryLog(dictionary.getId(), "新增", "新增字典明细：" + codeDescJoinShow(itemCode, itemDesc));

        // 清除缓存
        clearDictionaryCacheAfterCommit(dictionaryCode);

        return id;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDictionaryItem(DictionaryItemUpdateDTO param) {
        log.info("编辑字典明细,参数:{}", JSON.toJSONString(param));

        DictionaryItem record = dictionaryItemMapper.selectById(param.getId());
        AssertUtil.notNull(record, "字典明细不存在或已删除");

        String compareLog = ObjectCompareUtil.compareLog(record, param,
                ObjectCompareCustom.<DictionaryItemUpdateDTO, String>of(DictionaryItemUpdateDTO::getStatus,
                        value -> CodeDescEnumUtil.getDescByCode(DictionaryStatusEnum.class, value)));

        dictionaryItemMapper.update(new DictionaryItem(), Wrappers.<DictionaryItem>lambdaUpdate()
                .set(DictionaryItem::getItemDesc, param.getItemDesc())
                .set(DictionaryItem::getSortNumber, param.getSortNumber())
                .set(DictionaryItem::getStatus, param.getStatus())
                .set(DictionaryItem::getRemark, Objects.requireNonNullElse(param.getRemark(), ""))
                .eq(DictionaryItem::getId, record.getId()));

        Dictionary dictionary = baseMapper.selectOne(Wrappers.<Dictionary>lambdaQuery()
                .eq(Dictionary::getCode, record.getDictionaryCode()));

        if (StringUtils.isNotBlank(compareLog)) {
            addDictionaryLog(dictionary.getId(), "编辑", compareLog);
        }

        // 清除缓存
        clearDictionaryCacheAfterCommit(record.getDictionaryCode());

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateDictionaryItemStatus(DictionaryBatchUpdateStatusDTO param) {
        log.info("批量更新字典明细状态,参数:{}", JSON.toJSONString(param));
        List<Long> ids = validateBatchStatusUpdate(param);
        String status = param.getStatus();
        List<DictionaryItem> records = dictionaryItemMapper.selectByIds(ids);
        AssertUtil.isTrue(records.size() == ids.size(), "部分字典明细不存在或已删除");

        List<DictionaryItem> changedRecords = records.stream()
                .filter(record -> !status.equals(record.getStatus()))
                .toList();
        if (changedRecords.isEmpty()) {
            return true;
        }

        List<Long> changedIds = changedRecords.stream().map(DictionaryItem::getId).toList();
        int rows = dictionaryItemMapper.update(new DictionaryItem(), Wrappers.<DictionaryItem>lambdaUpdate()
                .set(DictionaryItem::getStatus, status)
                .in(DictionaryItem::getId, changedIds)
                .ne(DictionaryItem::getStatus, status));
        AssertUtil.isTrue(rows == changedIds.size(), "批量更新字典明细状态失败");

        List<String> dictionaryCodeList = records.stream().map(DictionaryItem::getDictionaryCode).distinct().toList();
        Map<String, Long> dictionaryCodeIdMap = getDictionaryCodeIdMap(dictionaryCodeList);

        String operation = CodeDescEnumUtil.getDescByCode(DictionaryStatusEnum.class, status);
        List<CommonLogAddDTO> logs = changedRecords.stream()
                .map(record -> buildDictionaryLog(
                        dictionaryCodeIdMap.get(record.getDictionaryCode()),
                        "修改状态",
                        operation + "字典明细：" + codeDescJoinShow(record.getItemCode(), record.getItemDesc())))
                .toList();
        AssertUtil.isTrue(commonLogService.batchAddLog(logs), "写入字典明细操作日志失败");

        changedRecords.stream().map(DictionaryItem::getDictionaryCode).distinct().forEach(this::clearDictionaryCacheAfterCommit);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictionaryItemsByIds(IdsDTO param) {
        log.info("删除字典明细,参数:{}", JSON.toJSONString(param));

        List<Long> requestedIds = param.getIds();
        AssertUtil.notEmpty(requestedIds, "明细ID不能为空");
        AssertUtil.isTrue(requestedIds.stream().allMatch(id -> id != null && id > 0), "明细ID必须为正数");

        List<Long> ids = requestedIds.stream().distinct().toList();
        List<DictionaryItem> records = dictionaryItemMapper.selectByIds(ids);
        AssertUtil.isTrue(records.size() == ids.size(), "字典明细不存在或已删除");

        // 传入实体以触发更新人自动填充，更新时间由数据库更新。
        int rows = dictionaryItemMapper.update(new DictionaryItem(), Wrappers.<DictionaryItem>lambdaUpdate()
                .setSql("`delete_flag` = `id`")
                .in(DictionaryItem::getId, ids));
        AssertUtil.isTrue(rows == ids.size(), "删除字典明细失败");

        List<String> dictionaryCodeList = records.stream().map(DictionaryItem::getDictionaryCode).distinct().toList();
        Map<String, Long> dictionaryCodeIdMap = getDictionaryCodeIdMap(dictionaryCodeList);

        List<CommonLogAddDTO> logs = records
                .stream()
                .map(record -> buildDictionaryLog(dictionaryCodeIdMap.get(record.getDictionaryCode()), "删除", "删除字典明细：" + codeDescJoinShow(record.getItemCode(), record.getItemDesc())))
                .toList();
        AssertUtil.isTrue(commonLogService.batchAddLog(logs), "写入字典明细操作日志失败");
        return true;
    }

    @Override
    public DictionaryItemDetailVO itemDetail(Long id) {
        DictionaryItem record = dictionaryItemMapper.selectById(id);
        if (Objects.isNull(record)) {
            return null;
        }
        DictionaryItemDetailVO vo = dictionaryConverter.toDictionaryItemDetailVO(record);
        vo.setStatusDesc(CodeDescEnumUtil.getDescByCode(DictionaryStatusEnum.class, vo.getStatus()));
        return vo;
    }

    /**
     * 查询待编辑或删除的字典，记录不存在或已删除时报错。
     */
    private Dictionary getRequiredDictionary(Long id) {
        Dictionary record = baseMapper.selectById(id);
        AssertUtil.notNull(record, "字典不存在或已删除");
        return record;
    }

    /**
     * 校验批量状态更新参数，返回去重后的目标 ID。
     */
    private List<Long> validateBatchStatusUpdate(DictionaryBatchUpdateStatusDTO param) {
        List<Long> ids = param.getIds();
        AssertUtil.notEmpty(ids, "ID列表不能为空");
        AssertUtil.isTrue(ids.stream().allMatch(id -> id != null && id > 0), "ID必须为正数");
        AssertUtil.isTrue(CodeDescEnumUtil.isExist(DictionaryStatusEnum.class, param.getStatus()), "状态必须为ENABLED或DISABLED");
        return ids.stream().distinct().toList();
    }

    /**
     * 在字典写入事务中记录操作日志，日志写入失败时回滚。
     */
    private void addDictionaryLog(Long id, String operation, String content) {
        AssertUtil.isTrue(commonLogService.addLog(buildDictionaryLog(id, operation, content)), "写入字典操作日志失败");
    }

    /**
     * 构建字典操作日志。
     */
    private CommonLogAddDTO buildDictionaryLog(Long id, String operation, String content) {
        return CommonLogAddDTO.builder()
                .businessType(CommonLogBusinessTypeEnum.DICTIONARY.getCode())
                .businessKey(String.valueOf(id))
                .operationType(operation)
                .content(content)
                .build();
    }

    /**
     * 获取字典编码和ID映射关系
     * @param dictionaryCodeList 字典编码集合
     */
    private Map<String, Long> getDictionaryCodeIdMap(List<String> dictionaryCodeList) {
        return baseMapper
                .selectList(Wrappers.<Dictionary>lambdaQuery()
                        .in(Dictionary::getCode, dictionaryCodeList)
                        .select(Dictionary::getId, Dictionary::getCode))
                .stream()
                .collect(Collectors.toMap(Dictionary::getCode, Dictionary::getId));
    }

    /**
     * 拼接编码和描述
     * @param code 编码
     * @param desc 描述
     */
    private String codeDescJoinShow(String code, String desc) {
        return "%s（%s）".formatted(code, desc);
    }

    /**
     * 提交事务后清除缓存
     * @param dictionaryCode 字典编码
     */
    private void clearDictionaryCacheAfterCommit(String dictionaryCode) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            clearDictionaryCache(dictionaryCode);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                clearDictionaryCache(dictionaryCode);
            }
        });
    }

    /**
     * 清除缓存
     * @param dictionaryCode 字典编码
     */
    private void clearDictionaryCache(String dictionaryCode) {
        redisHelper.deleteByCacheRegion(CacheRegion.DICTIONARY, dictionaryCode);
    }
}

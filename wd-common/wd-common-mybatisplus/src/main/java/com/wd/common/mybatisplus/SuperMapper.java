package com.wd.common.mybatisplus;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.function.ToIntFunction;

/**
 * 扩展 mybatis plus 方法
 *
 * @author huangwenda
 * @date 2021/9/28
 */
@Mapper
public interface SuperMapper<T> extends BaseMapper<T> {

    /**
     * 默认批次提交数量
     */
    int DEFAULT_BATCH_SIZE = 1000;

    /**
     * 批量插入
     *
     * @param entityList 实体列表
     * @return 影响行数
     */
    int insertBatchSomeColumn(Collection<T> entityList);

    /**
     * 批量更新
     * @param entityList 实体列表
     * @return 影响行数
     */
    int updateBatchSomeColumnById(Collection<T> entityList);

    /**
     * 更新所有字段，包含null
     *
     * @param entity 实体对象
     * @return 更新数量
     */
    int alwaysUpdateSomeColumnById(@Param(Constants.ENTITY) T entity);

    /**
     * 批量更新
     * @param entityList 实体列表
     * @param batchSize 提交数量
     * @return 影响行数
     */
    @Transactional(rollbackFor = Exception.class)
    default int updateBatchById(Collection<T> entityList, int batchSize) {
        return batchByGroup(entityList, this::updateBatchSomeColumnById, batchSize);
    }

    /**
     * 批量更新
     * @param entityList 实体列表
     * @return 影响行数
     */
    @Transactional(rollbackFor = Exception.class)
    default int updateBatchById(Collection<T> entityList) {
        return updateBatchById(entityList, DEFAULT_BATCH_SIZE);
    }


    /**
     * 批量处理
     * @param entityList 实体列表
     * @param groupFunction 回调方法
     * @param batchSize Size
     * @return int
     */
    private int batchByGroup(Collection<T> entityList, ToIntFunction<Collection<T>> groupFunction, int batchSize) {
        List<List<T>> groupByBatch = CollectionUtil.split(entityList, batchSize);
        int effectRowCount = 0;
        for (List<T> listGroup : groupByBatch) {
            effectRowCount += groupFunction.applyAsInt(listGroup);
        }
        return effectRowCount;
    }
}


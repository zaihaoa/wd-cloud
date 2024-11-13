package com.wd.common.mybatisplus;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 扩展 mybatis plus 方法
 *
 * @author huangwenda
 * @date 2021/9/28
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public interface SuperService<T> extends IService<T> {

    /**
     * 默认分割数
     */
    int DEFAULT_SPILT_SIZE = 5000;

    /**
     * 批量插入,没有事务
     *
     * @param entityList 实体列表
     * @return 影响行数
     */
    default int insertBatchSomeColumn(Collection<T> entityList) {
        return insertBatchSomeColumn(entityList, 0);
    }

    /**
     * 批量插入,没有事务
     *
     * @param entityList 实体列表
     * @return 影响行数
     */
    default int insertBatchSomeColumn(Collection<T> entityList, int splitSize) {
        if (entityList == null || entityList.isEmpty()) {
            return 0;
        }

        int finalSplitSize = splitSize > 0 ? splitSize : DEFAULT_SPILT_SIZE;

        SuperMapper superMapper = (SuperMapper) getBaseMapper();
        if (entityList.size() <= finalSplitSize) {
            return superMapper.insertBatchSomeColumn(entityList);
        } else {
            int totalInsertNum = 0;
            for (List<T> partList : split(entityList, finalSplitSize)) {
                totalInsertNum += superMapper.insertBatchSomeColumn(partList);
            }
            return totalInsertNum;
        }
    }

    /**
     * 更新所有字段，包含null
     *
     * @param entity 实体对象
     * @return 更新数量
     */
    default int alwaysUpdateSomeColumnById(T entity) {
        if (Objects.isNull(entity)) {
            return 0;
        }
        SuperMapper superMapper = (SuperMapper) getBaseMapper();
        return superMapper.alwaysUpdateSomeColumnById(entity);
    }

    // 分割数组
    private <E> List<List<E>> split(Collection<E> collection, int size) {
        final List<List<E>> result = new ArrayList<>();

        List<E> subList = new ArrayList<>(size);
        for (E t : collection) {
            if (subList.size() >= size) {
                result.add(subList);
                subList = new ArrayList<>(size);
            }
            subList.add(t);
        }
        result.add(subList);
        return result;
    }
}

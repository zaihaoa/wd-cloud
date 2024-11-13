package com.wd.common.mybatisplus;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * 扩展 mybatis plus 方法
 *
 * @author huangwenda
 * @date 2021/9/28
 */
@Mapper
public interface SuperMapper<T> extends BaseMapper<T> {

    /**
     * 批量插入
     *
     * @param entityList 实体列表
     * @return 影响行数
     */
    int insertBatchSomeColumn(Collection<T> entityList);


    /**
     * 更新所有字段，包含null
     *
     * @param entity 实体对象
     * @return 更新数量
     */
    int alwaysUpdateSomeColumnById(@Param(Constants.ENTITY) T entity);

}


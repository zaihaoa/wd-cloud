package com.wd.common.mybatisplus.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.wd.common.core.constants.CommonConstant;
import com.wd.common.core.constants.TableConstant;
import com.wd.common.core.context.SystemContext;
import com.wd.common.mybatisplus.MySqlInjector;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Optional;

@Configuration
public class MybatisPlusConfig {


    /**
     * 扩展批量插入和更新的方法
     */
    @Bean
    @ConditionalOnMissingBean
    public MySqlInjector getMySqlInjector() {
        return new MySqlInjector();
    }

    /**
     * 公共字段数据补充
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                Long userId = Optional.ofNullable(SystemContext.getUserId()).orElse(CommonConstant.SYSTEM_USER_ID);
                if (metaObject.hasGetter(TableConstant.ENTITY_CREATE_USER_ID)) {
                    this.setFieldValByName(TableConstant.ENTITY_CREATE_USER_ID, userId, metaObject);
                }
                if (metaObject.hasGetter(TableConstant.ENTITY_UPDATE_USER_ID)) {
                    this.setFieldValByName(TableConstant.ENTITY_UPDATE_USER_ID, userId, metaObject);
                }
                String createTimeField = TableConstant.ENTITY_CREATE_TIME;
                if (metaObject.hasSetter(createTimeField) && metaObject.getValue(createTimeField) == null) {
                    this.strictInsertFill(metaObject, createTimeField, LocalDateTime.class, LocalDateTime.now());
                }
                String updateTimeField = TableConstant.ENTITY_UPDATE_TIME;
                if (metaObject.hasSetter(updateTimeField) && metaObject.getValue(updateTimeField) == null) {
                    this.strictInsertFill(metaObject, updateTimeField, LocalDateTime.class, LocalDateTime.now());
                }
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                if (metaObject.hasGetter(TableConstant.ENTITY_UPDATE_USER_ID)) {
                    this.setFieldValByName(TableConstant.ENTITY_UPDATE_USER_ID, Optional.ofNullable(SystemContext.getUserId()).orElse(CommonConstant.SYSTEM_USER_ID), metaObject);
                }
                if (metaObject.hasSetter(TableConstant.ENTITY_UPDATE_TIME)) {
                    this.setFieldValByName(TableConstant.ENTITY_UPDATE_TIME, LocalDateTime.now(), metaObject);
                }
            }
        };
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL)); // 如果配置多个插件, 切记分页最后添加
        return interceptor;
    }
}

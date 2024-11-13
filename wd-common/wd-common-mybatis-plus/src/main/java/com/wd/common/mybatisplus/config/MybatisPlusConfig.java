package com.wd.common.mybatisplus.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.wd.common.core.constants.TableConstant;
import com.wd.common.core.constants.CommonConstant;
import com.wd.common.core.context.SystemContext;
import com.wd.common.mybatisplus.MySqlInjector;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class MybatisPlusConfig {

    /**
     * 分页插件
     */
    /*@Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setDbType(DbType.MYSQL);
        // 开启 count 的 join 优化,只针对部分 left join
        paginationInnerInterceptor.setOverflow(true);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInnerInterceptor.setMaxLimit(-1L);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }*/

    /**
     * 扩展批量插入和更新的方法
     */
    @Bean
    @ConditionalOnMissingBean
    public MySqlInjector getMySqlInjector() {
        return new MySqlInjector();
    }

    /**
     * todo 补充当前线程用户id
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
                if (metaObject.hasGetter(TableConstant.ENTITY_CREATE_UPDATE_ID)) {
                    this.setFieldValByName(TableConstant.ENTITY_CREATE_UPDATE_ID, userId, metaObject);
                }
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                if (metaObject.hasGetter(TableConstant.ENTITY_CREATE_UPDATE_ID)) {
                    this.setFieldValByName(TableConstant.ENTITY_CREATE_UPDATE_ID, Optional.ofNullable(SystemContext.getUserId()).orElse(CommonConstant.SYSTEM_USER_ID), metaObject);
                }
            }
        };
    }
}

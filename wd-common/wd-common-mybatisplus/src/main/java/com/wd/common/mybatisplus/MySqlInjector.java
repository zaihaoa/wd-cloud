package com.wd.common.mybatisplus;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.AlwaysUpdateSomeColumnById;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import com.wd.common.core.constants.TableConstant;

import java.util.List;

/**
 * 自定义注册器
 *
 * @author huangwenda
 * @date 2021/3/11
 */
public class MySqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);

        // 添加InsertBatchSomeColumn方法,过滤掉create_time和update_time字段
        methodList.add(new InsertBatchSomeColumn(i -> !TableConstant.TABLE_CREATE_TIME.equals(i.getColumn()) && !TableConstant.TABLE_UPDATE_TIME.equals(i.getColumn())));

        // 所有字段都更新的方法
        methodList.add(new AlwaysUpdateSomeColumnById(i ->
                i.getFieldFill() != FieldFill.INSERT
                        && !TableConstant.TABLE_CREATE_USER_ID.equals(i.getColumn())
                        && !TableConstant.TABLE_CREATE_TIME.equals(i.getColumn())
                        && !TableConstant.TABLE_UPDATE_TIME.equals(i.getColumn())));
        return methodList;
    }
}

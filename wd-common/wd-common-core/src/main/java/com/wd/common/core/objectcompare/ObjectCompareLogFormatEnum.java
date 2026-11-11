package com.wd.common.core.objectcompare;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对象比较log输出格式
 * @author huangwenda
 * @date 2023/3/8 16:15
 **/
@Getter
@AllArgsConstructor
public enum ObjectCompareLogFormatEnum {
    SIMPLE("name：[oldValue]变更为[newValue]", "name：设置为[newValue]", "name：[oldValue]变更为空", "，", "name：新增[collectionAddValue]", "name：移除[collectionRemoveValue]", "、"),
    FORMAT_1("name：【oldValue】变更为【newValue】", "name：设置为【newValue】", "name：【oldValue】变更为空", "，", "name：新增【collectionAddValue】", "name：移除【collectionRemoveValue】", "、"),
    FORMAT_2("name：oldValue -> newValue", "name：newValue", "name：删除oldValue", "，", "name：新增collectionAddValue", "name：移除collectionRemoveValue", "、"),
    ;

    /**
     * 更新格式
     */
    private final String updateFormat;

    /**
     * 新增格式
     */
    private final String addFormat;

    /**
     * 移除格式
     */
    private final String removeFormat;

    /**
     * 拼接字符
     */
    private final String joinChar;

    /**
     * 集合新增格式
     */
    private final String collectionAddFormat;

    /**
     * 集合更新格式
     */
    private final String collectionRemoveFormat;

    /**
     * 集合拼接字符
     */
    private final String collectionJoinChar;

}

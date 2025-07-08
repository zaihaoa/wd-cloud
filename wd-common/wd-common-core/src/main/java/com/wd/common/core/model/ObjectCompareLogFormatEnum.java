package com.wd.common.core.model;

/**
 * 对象比较log输出格式
 * @author huangwenda
 * @date 2023/3/8 16:15
 **/
public enum ObjectCompareLogFormatEnum {
    SIMPLE("name：[oldValue]变更为[newValue]", "name：设置为[newValue]", "name：[oldValue]变更为空", "，"),
    FORMAT_1("name：【oldValue】变更为【newValue】", "name：设置为【newValue】", "name：【oldValue】变更为空", "，"),
    FORMAT_2("name：oldValue -> newValue", "name：newValue", "name：删除oldValue", "，"),
    ;

    /**
     * 更新格式
     */
    private final String updateFormat;

    /**
     * 更新格式
     */
    private final String addFormat;

    /**
     * 更新格式
     */
    private final String removeFormat;

    /**
     * 拼接字符
     */
    private final String joinChar;

    ObjectCompareLogFormatEnum(String updateFormat, String addFormat, String removeFormat, String joinChar) {
        this.updateFormat = updateFormat;
        this.addFormat = addFormat;
        this.removeFormat = removeFormat;
        this.joinChar = joinChar;
    }

    public String getUpdateFormat() {
        return updateFormat;
    }

    public String getAddFormat() {
        return addFormat;
    }

    public String getRemoveFormat() {
        return removeFormat;
    }

    public String getJoinChar() {
        return joinChar;
    }
}

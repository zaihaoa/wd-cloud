package com.wd.common.excel.valid;

/**
 * 验证错误消息常量类
 *
 * @author huangwenda
 * @date 2025/11/4
 **/
public class ValidConstant {

    private ValidConstant() {
        // 私有构造函数，防止实例化
    }

    /**
     * 字段不能为空
     */
    public static final String FIELD_CANNOT_BE_BLANK = "%s不能为空";

    /**
     * 数值格式不正确
     */
    public static final String NUMBER_FORMAT_ERROR = "%s格式不正确,请填写数值";

    /**
     * 整数格式不正确
     */
    public static final String INT_FORMAT_ERROR = "%s格式不正确,请填写整数";

    /**
     * 日期格式不正确
     */
    public static final String DATE_FORMAT_ERROR = "%s格式不正确，示例：2025-01-01";

    /**
     * 日期时间格式不正确
     */
    public static final String DATE_TIME_FORMAT_ERROR = "%s格式不正确，示例：2025-01-01 00:00:00";

    /**
     * 字符串长度超过限制
     */
    public static final String STRING_LENGTH_EXCEEDS_LIMIT = "%s长度不能超过%d";

    /**
     * 值必须小于指定值
     */
    public static final String VALUE_MUST_BE_LESS_THAN = "%s要小于%s";

    /**
     * 值必须小于等于指定值
     */
    public static final String VALUE_MUST_BE_LESS_OR_EQUAL = "%s要小于等于%s";

    /**
     * 值必须大于指定值
     */
    public static final String VALUE_MUST_BE_GREATER_THAN = "%s要大于%s";

    /**
     * 值必须大于等于指定值
     */
    public static final String VALUE_MUST_BE_GREATER_OR_EQUAL = "%s要大于等于%s";

    /**
     * 值必须在枚举列表中
     */
    public static final String VALUE_MUST_BE_IN_ENUM_LIST = "%s只能选择[%s]";
}

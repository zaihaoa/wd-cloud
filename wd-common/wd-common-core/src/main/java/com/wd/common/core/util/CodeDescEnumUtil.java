package com.wd.common.core.util;

import com.wd.common.core.CodeDescEnum;
import com.wd.common.core.SelectCombobox;

import java.util.*;

/**
 * @author huangwenda
 * @date 2023/1/15 11:14
 **/
public class CodeDescEnumUtil {
    /**
     * 判断枚举值是否存与指定枚举类中
     *
     * @param enumClass 枚举类
     * @param code      枚举值
     * @param <E>       枚举类型
     * @param <V>       值类型
     * @return true：存在
     */
    public static <E extends Enum<? extends CodeDescEnum<V>>, V> boolean isExist(Class<E> enumClass, V code) {
        return Objects.nonNull(getEnumByCode(enumClass, code));
    }

    /**
     * 根据枚举值获取其对应的名字
     *
     * @param enumClass 枚举
     * @param code      code值
     * @return 枚举描述
     */
    @SuppressWarnings("unchecked")
    public static <E extends Enum<? extends CodeDescEnum<V>>, V> String getDescByCode(Class<E> enumClass, V code) {
        if (code == null) {
            return null;
        }
        for (Enum<? extends CodeDescEnum<V>> e : enumClass.getEnumConstants()) {
            if (((CodeDescEnum<V>) e).getCode().equals(code)) {
                return ((CodeDescEnum<V>) e).getDesc();
            }
        }
        return null;
    }


    /**
     * 根据枚举值获取对应的枚举对象
     *
     * @param enums 枚举列表
     * @param code  code值
     * @return 枚举对象
     */
    @SuppressWarnings("unchecked")
    public static <E extends Enum<? extends CodeDescEnum<V>>, V> E getEnumByCode(E[] enums, V code) {
        if (enums == null || code == null) {
            return null;
        }
        for (E e : enums) {
            if (((CodeDescEnum<V>) e).getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 根据枚举值获取对应的枚举对象
     *
     * @param enumClass 枚举class
     * @param code      code值
     * @return 枚举对象
     */
    public static <E extends Enum<? extends CodeDescEnum<V>>, V> E getEnumByCode(Class<E> enumClass, V code) {
        if (enumClass == null || code == null) {
            return null;
        }
        return getEnumByCode(enumClass.getEnumConstants(), code);
    }

    /**
     * 把枚举值转化为下拉框对象
     *
     * @param enumClass 枚举class
     * @return 下拉框对象集合
     */
    public static <E extends Enum<? extends CodeDescEnum<V>>, V> List<SelectCombobox> convertSelectCombobox(Class<E> enumClass) {
        return convertSelectCombobox(enumClass, null);
    }

    /**
     * 把枚举值转化为下拉框对象
     *
     * @param enumClass 枚举class
     * @return 下拉框对象集合
     */
    public static <E extends Enum<? extends CodeDescEnum<V>>, V> List<SelectCombobox> convertSelectCombobox(Class<E> enumClass, Collection<E> excludeEnums) {
        if (enumClass == null) {
            return new ArrayList<>();
        }
        List<SelectCombobox> selectComboboxList = new ArrayList<>();
        for (E e : enumClass.getEnumConstants()) {
            if (excludeEnums != null && excludeEnums.contains(e)) {
                continue;
            }
            selectComboboxList.add(new SelectCombobox(((CodeDescEnum) e).getCode().toString(), ((CodeDescEnum) e).getDesc()));
        }
        return selectComboboxList;
    }
}

package com.wd.common.core.util;




import com.wd.common.core.model.CodeDescEnum;
import com.wd.common.core.model.SelectCombobox;

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
     * @return true：存在
     */
    public static <E extends Enum<? extends CodeDescEnum>, V> boolean isExist(Class<E> enumClass, String code) {
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
    public static <E extends Enum<? extends CodeDescEnum>> String getDescByCode(Class<E> enumClass, String code) {
        if (code == null) {
            return null;
        }
        for (Enum<? extends CodeDescEnum> e : enumClass.getEnumConstants()) {
            if (((CodeDescEnum) e).getCode().equals(code)) {
                return ((CodeDescEnum) e).getDesc();
            }
        }
        return null;
    }

    /**
     * 根据对应的名字获取枚举值
     *
     * @param enumClass 枚举
     * @param desc      对应的名字
     * @return 枚举描述
     */
    @SuppressWarnings("unchecked")
    public static <E extends Enum<? extends CodeDescEnum>> String getCodeByDesc(Class<E> enumClass, String desc) {
        if (desc == null) {
            return null;
        }
        for (Enum<? extends CodeDescEnum> e : enumClass.getEnumConstants()) {
            if (((CodeDescEnum) e).getDesc().equals(desc)) {
                return ((CodeDescEnum) e).getCode();
            }
        }
        return null;
    }

    public static <E extends Enum<? extends CodeDescEnum>> Map<String, String> codeDescMap(Class<E> enumClass) {
        E[] enumConstants = enumClass.getEnumConstants();
        Map<String, String> result = new HashMap<>(enumConstants.length);

        for (Enum<? extends CodeDescEnum> e : enumConstants) {
            result.put(((CodeDescEnum) e).getCode(), ((CodeDescEnum) e).getDesc());
        }
        return result;
    }

    public static <E extends Enum<? extends CodeDescEnum>> Map<String, E> codeEnumMap(Class<E> enumClass) {
        E[] enumConstants = enumClass.getEnumConstants();
        Map<String, E> result = new HashMap<>(enumConstants.length);

        for (E e : enumConstants) {
            result.put(((CodeDescEnum) e).getCode(), e);
        }
        return result;
    }

    /**
     * 根据枚举值获取对应的枚举对象
     *
     * @param enums 枚举列表
     * @param code  code值
     * @return 枚举对象
     */
    @SuppressWarnings("unchecked")
    public static <E extends Enum<? extends CodeDescEnum>> E getEnumByCode(E[] enums, String code) {
        if (enums == null || code == null) {
            return null;
        }
        for (E e : enums) {
            if (((CodeDescEnum) e).getCode().equals(code)) {
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
    public static <E extends Enum<? extends CodeDescEnum>> E getEnumByCode(Class<E> enumClass, String code) {
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
    public static <E extends Enum<? extends CodeDescEnum>> List<SelectCombobox> convertSelectCombobox(Class<E> enumClass) {
        return convertSelectCombobox(enumClass, null);
    }

    /**
     * 把枚举值转化为下拉框对象
     *
     * @param enumClass 枚举class
     * @return 下拉框对象集合
     */
    public static <E extends Enum<? extends CodeDescEnum>> List<SelectCombobox> convertSelectCombobox(Class<E> enumClass, Collection<E> excludeEnums) {
        if (enumClass == null) {
            return new ArrayList<>();
        }
        List<SelectCombobox> selectComboboxList = new ArrayList<>();
        for (E e : enumClass.getEnumConstants()) {
            if (excludeEnums != null && excludeEnums.contains(e)) {
                continue;
            }
            selectComboboxList.add(new SelectCombobox(((CodeDescEnum) e).getCode(), ((CodeDescEnum) e).getDesc()));
        }
        return selectComboboxList;
    }
}

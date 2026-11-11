package com.wd.common.core.objectcompare;


import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @author huangwenda
 * @date 2023/3/8 14:18
 **/
@Slf4j
public class ObjectCompareUtil {

    /**
     * 属性比较
     *
     * @param source 源数据对象,原始数据对象
     * @param target 目标数据对象,修改后的数据对象
     * @return 对应属性值的比较变化
     */
    public static String compareLog(Object source, Object target) {
        return compareLog(source, target, ObjectCompareLogFormatEnum.SIMPLE, null);
    }

    public static <T> String compareLog(Object source, Object target, ObjectCompareCustom... objectCompareCustomList) {
        return compareLog(source, target, ObjectCompareLogFormatEnum.SIMPLE, null, objectCompareCustomList);
    }

    /**
     * 属性比较
     *
     * @param source  源数据对象,原始数据对象
     * @param target  目标数据对象,修改后的数据对象
     * @param logEnum 日志格式枚举
     * @return 对应属性值的比较变化
     */
    public static String compareLog(Object source, Object target, ObjectCompareLogFormatEnum logEnum) {
        return compareLog(source, target, logEnum, null);
    }

    /**
     * 属性比较
     *
     * @param source              源数据对象,原始数据对象
     * @param target              目标数据对象,修改后的数据对象
     * @param ignoreFields 忽略比较的字段
     * @return 对应属性值的比较变化
     */
    public static String compareLog(Object source, Object target, Collection<String> ignoreFields) {
        return compareLog(source, target, ObjectCompareLogFormatEnum.SIMPLE, ignoreFields);
    }

    /**
     * 属性比较
     *
     * @param source                    源数据对象,原始数据对象
     * @param target                    目标数据对象,修改后的数据对象
     * @param ignoreFields              忽略比较的字段
     * @param objectCompareCustomList   自定义解析字段
     * @return 对应属性值的比较变化
     */
    public static String compareLog(Object source,
                                    Object target,
                                    Collection<String> ignoreFields,
                                    ObjectCompareCustom... objectCompareCustomList) {
        List<ObjectCompareResult> compareResultList = compare(source, target, ignoreFields, objectCompareCustomList);
        if (compareResultList.isEmpty()) {
            return "";
        }
        return getLogContent(compareResultList, ObjectCompareLogFormatEnum.SIMPLE);
    }

    /**
     * 属性比较
     *
     * @param source                    源数据对象,原始数据对象
     * @param target                    目标数据对象,修改后的数据对象
     * @param ignoreFields              忽略比较的字段
     * @param objectCompareCustomList   自定义解析字段
     * @param logEnum                   日志格式枚举
     * @return 对应属性值的比较变化
     */
    public static String compareLog(Object source,
                                    Object target,
                                    ObjectCompareLogFormatEnum logEnum,
                                    Collection<String> ignoreFields,
                                    ObjectCompareCustom... objectCompareCustomList) {
        List<ObjectCompareResult> compareResultList = compare(source, target, ignoreFields, objectCompareCustomList);
        if (compareResultList.isEmpty()) {
            return "";
        }
        return getLogContent(compareResultList, logEnum);
    }

    private static String getLogContent(List<ObjectCompareResult> compareResultList, ObjectCompareLogFormatEnum logEnum) {
        if (compareResultList.isEmpty()) {
            return "";
        }
        int size = compareResultList.size();
        StringBuilder sb = new StringBuilder(size * 15);

        for (int i = 0; i < size; i++) {
            ObjectCompareResult compareResult = compareResultList.get(i);
            String name = compareResult.getName();
            String oldValue = compareResult.getOldValue();
            String newValue = compareResult.getNewValue();

            if (Objects.equals(oldValue, newValue)) {
                continue;
            }

            // 更新
            if (!isBlank(oldValue) && !isBlank(newValue)) {
                String logFormat = logEnum.getUpdateFormat();
                logFormat = logFormat.replace("name", Matcher.quoteReplacement(name));
                logFormat = logFormat.replace("oldValue", oldValue);
                logFormat = logFormat.replace("newValue", newValue);
                sb.append(logFormat);
            }

            // 新增
            if (isBlank(oldValue) && !isBlank(newValue)) {
                String logFormat = logEnum.getAddFormat();
                logFormat = logFormat.replace("name", name);
                logFormat = logFormat.replace("newValue", newValue);
                sb.append(logFormat);
            }

            // 移除
            if (!isBlank(oldValue) && isBlank(newValue)) {
                String logFormat = logEnum.getRemoveFormat();
                logFormat = logFormat.replace("name", name);
                logFormat = logFormat.replace("oldValue", oldValue);
                sb.append(logFormat);
            }

            if (i != (size - 1)) {
                sb.append(logEnum.getJoinChar());
            }
        }
        return sb.toString();
    }

    /**
     * 属性比较
     *
     * @param source 源数据对象,原始数据对象
     * @param target 目标数据对象,修改后的数据对象
     * @return 对应属性值的比较变化
     */
    public static List<ObjectCompareResult> compare(Object source, Object target) {
        return compare(source, target, null, null);
    }

    /**
     * 属性比较
     *
     * @param source              源数据对象,原始数据对象
     * @param target              目标数据对象,修改后的数据对象
     * @param ignoreFields 忽略比较的字段
     * @return 对应属性值的比较变化
     */
    public static List<ObjectCompareResult> compare(Object source, Object target, Collection<String> ignoreFields, ObjectCompareCustom... objectCompareCustomList) {
        if (Objects.isNull(source) && Objects.isNull(target)) {
            return new ArrayList<>();
        }
        Map<String, ObjectCompareField> sourceMap = getSourceFiledValueMap(source);
        Map<String, ObjectCompareField> targetMap = getTargetFiledValueMap(target);
        if (sourceMap.isEmpty() || targetMap.isEmpty()) {
            return new ArrayList<>();
        }

        return doCompare(sourceMap, targetMap, ignoreFields, objectCompareCustomList);
    }

    private static List<ObjectCompareResult> doCompare(Map<String, ObjectCompareField> sourceMap,
                                                       Map<String, ObjectCompareField> targetMap,
                                                       Collection<String> ignoreFields,
                                                       ObjectCompareCustom... objectCompareCustomList) {
        List<ObjectCompareResult> resultList = new ArrayList<>();
        targetMap.forEach((key, val) -> {
            if (Objects.isNull(ignoreFields) || !ignoreFields.contains(key)) {
                ObjectCompareField source = sourceMap.get(key);
                ObjectCompareField target = targetMap.get(key);
                if (Objects.nonNull(source)) {
                    String name = StrUtil.isBlank(target.getAnnotationName()) ? key : target.getAnnotationName();
                    ObjectCompareCustom objectCompareCustom = customConvert(key, objectCompareCustomList);
                    if (Objects.isNull(objectCompareCustom)) {
                        String sourceValue = source.getValue();
                        String targetValue = target.getValue();
                        if (!Objects.equals(sourceValue, targetValue)) {
                            resultList.add(new ObjectCompareResult(name, sourceValue, targetValue));
                        }
                    } else {
                        Object sourceOriginValue = source.getOriginValue();
                        String sourceValue = Objects.isNull(sourceOriginValue) ? "" : (String) objectCompareCustom.getConvertFunc().apply(sourceOriginValue);

                        Object targetOriginValue = target.getOriginValue();
                        String targetValue = Objects.isNull(targetOriginValue) ? "" : (String) objectCompareCustom.getConvertFunc().apply(targetOriginValue);

                        if (!Objects.equals(sourceValue, targetValue)) {
                            resultList.add(new ObjectCompareResult(name, sourceValue, targetValue));
                        }
                    }
                }
            }
        });

        return resultList;
    }

    private static ObjectCompareCustom customConvert(String fieldName, ObjectCompareCustom... objectCompareCustomList) {
        if (objectCompareCustomList == null || objectCompareCustomList.length == 0) {
            return null;
        }
        for (ObjectCompareCustom objectCompareCustom : objectCompareCustomList) {
            String fieldNameTemp = LambdaUtil.getFieldName(objectCompareCustom.getFieldFunc());
            if (Objects.equals(fieldName, fieldNameTemp)) {
                return objectCompareCustom;
            }
        }
        return null;
    }

    private static Map<String, ObjectCompareField> getTargetFiledValueMap(Object obj) {
        if (Objects.isNull(obj)) {
            return new HashMap<>();
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        if (fields.length == 0) {
            return new HashMap<>();
        }
        Map<String, ObjectCompareField> map = new LinkedHashMap<>();
        for (Field field : fields) {
            ObjectCompare compareAnnotation = field.getAnnotation(ObjectCompare.class);
            if (Objects.isNull(compareAnnotation) || compareAnnotation.ignore()) {
                continue;
            }
            field.setAccessible(true);
            try {
                String name = field.getName();
                Object value = field.get(obj);
                // 值转化为string类型
                String stringValue = getFileStringValue(field, value);

                String annotationName = Optional.of(compareAnnotation).map(ObjectCompare::value).orElse(name);

                map.put(name, new ObjectCompareField(name, stringValue, value, annotationName));
            } catch (IllegalArgumentException | IllegalAccessException ignored) {
            }
        }
        return map;
    }

    private static Map<String, ObjectCompareField> getSourceFiledValueMap(Object obj) {
        if (Objects.isNull(obj)) {
            return new HashMap<>();
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        if (fields.length == 0) {
            return new HashMap<>();
        }
        Map<String, ObjectCompareField> map = new LinkedHashMap<>();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                String name = field.getName();
                Object value = field.get(obj);
                // 值转化为string类型
                String stringValue = getFileStringValue(field, value);
                map.put(name, new ObjectCompareField(name, stringValue, value, null));
            } catch (IllegalArgumentException | IllegalAccessException ignored) {
            }
        }
        return map;
    }

    private static String getFileStringValue(Field field, Object val) throws IllegalArgumentException, IllegalAccessException {
        if (val == null) {
            return "";
        }
        String typeName = field.getType().getName();

        if (BigDecimal.class.getTypeName().equals(typeName)) {
            return ((BigDecimal) val).stripTrailingZeros().toPlainString();
        } else if (Date.class.getTypeName().equals(typeName)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(((Date) val));
        } else if (LocalDateTime.class.getTypeName().equals(typeName)) {
            return ((LocalDateTime) val).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } else if (val instanceof Collection<?> collection) {
            return collection.isEmpty() ? "" : String.valueOf(collection);
        } else {
            return String.valueOf(val);
        }
    }

    public static boolean isBlank(final CharSequence cs) {
        final int strLen = cs == null ? 0 : cs.length();
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 连接三个字符串，格式为：prefix + delimiter + suffix
     *
     * @param prefix 前缀字符串
     * @param delimiter 分隔符字符串
     * @param suffix 后缀字符串
     * @return 连接后的字符串，如果prefix或suffix为空则返回对应的非空部分
     */
    public static String joinStr(String prefix, String delimiter, String suffix) {
        if (isBlank(prefix)) {
            return suffix;
        }
        if (isBlank(suffix)) {
            return prefix;
        }
        return prefix + delimiter + suffix;
    }

    /**
     * 构建log内容
     *
     * @param name 字段名称
     * @param oldValue 旧值
     * @param newValue 新值
     * @return log内容
     */
    public static String buildLogContent(String name, String oldValue, String newValue) {
        return getLogContent(List.of(new ObjectCompareResult(name, oldValue, newValue)), ObjectCompareLogFormatEnum.SIMPLE);
    }

    /**
     * 构建log内容
     *
     * @param name 字段名称
     * @param oldValue 旧值
     * @param newValue 新值
     * @param logEnum  log输出格式
     * @return log内容
     */
    public static String buildLogContent(String name, String oldValue, String newValue, ObjectCompareLogFormatEnum logEnum) {
        return getLogContent(List.of(new ObjectCompareResult(name, oldValue, newValue)), logEnum);
    }

    /**
     * 构建log内容
     * @param compareResultList 对比结果
     * @param logEnum log输出格式
     * @return log内容
     */
    public static String buildLogContent(List<ObjectCompareResult> compareResultList, ObjectCompareLogFormatEnum logEnum) {
        return getLogContent(compareResultList, logEnum);
    }
}

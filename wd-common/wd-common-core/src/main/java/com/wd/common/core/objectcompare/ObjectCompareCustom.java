package com.wd.common.core.objectcompare;

import cn.hutool.core.lang.func.Func1;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.function.Function;

/**
 * @author huangwenda
 * @date 2023/3/8 14:17
 **/
@Schema(description = "对象比较自定义比较器")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ObjectCompareCustom<T, R> {
    /**
     * 字段名
     */
    private Func1<T, ?> fieldFunc;

    /**
     * 字段值转换
     */
    private Function<R, String> convertFunc;

    /**
     * 集合字段值转换
     */
    private Function<R, Map<String, String>> collectionConvertFunc;

    public static <T, R> ObjectCompareCustom<T, R> of(Func1<T, ?> fieldFunc, Function<R, String> convertFunc) {
        return new ObjectCompareCustom<>(fieldFunc, convertFunc, null);
    }

    public static <T, R> ObjectCompareCustom<T, R> ofCollection(Func1<T, ?> fieldFunc, Function<R, Map<String, String>> collectionConvertFunc) {
        return new ObjectCompareCustom<>(fieldFunc, null, collectionConvertFunc);
    }
}

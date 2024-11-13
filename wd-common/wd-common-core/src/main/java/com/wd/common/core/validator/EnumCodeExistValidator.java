package com.wd.common.core.validator;

import com.wd.common.core.CodeDescEnum;
import com.wd.common.core.annotions.EnumCodeExist;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AssertRequestReportTypeExist实现类
 *
 * @author huangwenda
 * @date 2021/10/23
 **/
public class EnumCodeExistValidator implements ConstraintValidator<EnumCodeExist, Object> {

    /**
     * 对应的枚举类
     */
    private Class<? extends Enum> enumClass;
    /**
     * 是否必填
     */
    private boolean required;

    @Override
    public void initialize(EnumCodeExist constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        // 如果是必填，值为空返回false，如果不是必填，值为空返回true
        if (value == null) {
            return !required;
        }

        if (enumClass.isEnum()) {
            if (value instanceof Collection<?>) {
                Collection values = (Collection) value;
                List enums = Arrays.stream(enumClass.getEnumConstants()).filter(v -> v instanceof CodeDescEnum).map(v -> ((CodeDescEnum) v).getCode()).collect(Collectors.toList());
                return enums.containsAll(values);
            } else {
                return Arrays.stream(enumClass.getEnumConstants()).anyMatch(v -> v instanceof CodeDescEnum && ((CodeDescEnum) v).getCode().equals(value));
            }
        }
        return false;
    }
}

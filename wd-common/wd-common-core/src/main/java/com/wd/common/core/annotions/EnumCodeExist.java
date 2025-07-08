package com.wd.common.core.annotions;

import com.wd.common.core.model.CodeDescEnum;
import com.wd.common.core.validator.EnumCodeExistValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


/**
 * 枚举传参校验，检验参数是否是枚举中的值，通过 paramValidator 方法检验
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumCodeExistValidator.class)
public @interface EnumCodeExist {

    /**
     * 默认提示信息
     */
    String message() default "输入项不存在";

    /**
     * 枚举类,必须实现{@link CodeDescEnum}接口,传入的值与{@link CodeDescEnum#getCode()}做比较
     */
    Class<? extends Enum> enumClass();

    /**
     * 是否必填
     */
    boolean required() default false;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

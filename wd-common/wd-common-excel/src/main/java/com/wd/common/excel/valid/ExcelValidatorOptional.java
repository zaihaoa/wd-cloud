package com.wd.common.excel.valid;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 *
 * @author huangwenda
 * @date 2025/11/6 18:36
 **/
@Getter
@Setter
public class ExcelValidatorOptional {

    @Schema(description = "是否需要校验")
    private Boolean valid;

    @Schema(description = "允许的值")
    private List<String> allowableValues;

    public static ExcelValidatorOptional of(boolean valid) {
        ExcelValidatorOptional optional = new ExcelValidatorOptional();
        optional.setValid(valid);
        return optional;
    }

    public static ExcelValidatorOptional of(List<String> allowableValues) {
        ExcelValidatorOptional optional = new ExcelValidatorOptional();
        optional.setAllowableValues(allowableValues);
        return optional;
    }

    public static ExcelValidatorOptional of(boolean valid, List<String> allowableValues) {
        ExcelValidatorOptional optional = new ExcelValidatorOptional();
        optional.setValid(valid);
        optional.setAllowableValues(allowableValues);
        return optional;
    }
}

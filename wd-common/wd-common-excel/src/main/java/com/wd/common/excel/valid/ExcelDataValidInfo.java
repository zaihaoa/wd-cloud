package com.wd.common.excel.valid;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author huangwenda
 * @date 2023/3/8 14:17
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExcelDataValidInfo {

    @Schema(description = "字段名")
    private String fieldName;

    @Schema(description = "字段值")
    private String fieldValue;

    @Schema(description = "注解名称")
    private String annotationName;

    @Schema(description = "字符串-是否不能为空")
    private boolean stringNotBlank;

    @Schema(description = "字符串-最小长度,包含minLength")
    private int stringMinLength;

    @Schema(description = "字符串-最大长度,包含maxLength")
    private int stringMaxLength;

    @Schema(description = "字符串-允许的值")
    private List<String> stringAllowableValues;

    @Schema(description = "int-是否不能为空")
    private boolean intNotNull;

    @Schema(description = "int-小于")
    private int intLess;

    @Schema(description = "int-小于等于")
    private int intLessOrEqual;

    @Schema(description = "int-大于")
    private int intGreater;

    @Schema(description = "int-大于等于")
    private int intGreaterOrEqual;

    @Schema(description = "BigDecimal-是否不能为空")
    private boolean bigDecimalNotNull;

    @Schema(description = "BigDecimal-小于")
    private BigDecimal bigDecimalLess;

    @Schema(description = "BigDecimal-小于等于")
    private BigDecimal bigDecimalLessOrEqual;

    @Schema(description = "BigDecimal-大于")
    private BigDecimal bigDecimalGreater;

    @Schema(description = "BigDecimal-大于等于")
    private BigDecimal bigDecimalGreaterOrEqual;

    @Schema(description = "LocalDate-是否不能为空")
    private boolean localDateNotNull;

    @Schema(description = "LocalDateTime-是否不能为空")
    private boolean localDateTimeNotNull;

}

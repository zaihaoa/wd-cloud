package com.wd.common.excel.valid;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import com.wd.common.excel.valid.annotations.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 验证规则基类
 *
 * @author huangwenda
 * @date 2025/11/4 15:38
 **/
public class ExcelValidator<T> {

    @Schema(description = "Excel数据对象")
    @Getter
    @Setter
    private T excelData;

    @Schema(description = "验证错误信息集合")
    @Getter
    @Setter
    private List<String> errorList = new ArrayList<>();

    private final Map<String, ExcelDataValidInfo> excelDataValidInfoMap = new HashMap<>();

    public ExcelValidator(T excelData) {
        this.excelData = excelData;
        buildExcelDataValidInfoMap();
    }


    public ValidResult<String> validString(Func1<T, ?> fieldFunc) {
        return validString(fieldFunc, null);
    }

    public ValidResult<String> validString(Func1<T, ?> fieldFunc, ExcelValidatorOptional optional) {
        String fieldName = LambdaUtil.getFieldName(fieldFunc);

        ExcelDataValidInfo excelDataValidInfo = excelDataValidInfoMap.get(fieldName);
        if (excelDataValidInfo == null) {
            return new ValidResult<>(true, null, null);
        }

        String fieldValue = excelDataValidInfo.getFieldValue();
        String annotationName = excelDataValidInfo.getAnnotationName();
        boolean notBlank = excelDataValidInfo.isStringNotBlank();
        int maxLength = excelDataValidInfo.getStringMaxLength();

        if (Objects.nonNull(optional) && !Optional.ofNullable(optional.getValid()).orElse(true)) {
            return new ValidResult<>(true, fieldValue, null);
        }

        List<String> allowableValues = null;
        if (Objects.nonNull(optional) && CollUtil.isNotEmpty(optional.getAllowableValues())) {
            allowableValues = optional.getAllowableValues();
        } else {
            allowableValues = excelDataValidInfo.getStringAllowableValues();
        }

        if (StringUtils.isBlank(fieldValue)) {
            if (notBlank) {
                String error = String.format(ValidConstant.FIELD_CANNOT_BE_BLANK, annotationName);
                errorList.add(error);
                return new ValidResult<>(false, null, error);
            }
            return new ValidResult<>(true, null, null);
        }

        if (maxLength > 0 && fieldValue.length() > maxLength) {
            String error = String.format(ValidConstant.STRING_LENGTH_EXCEEDS_LIMIT, annotationName, maxLength);
            errorList.add(error);
            return new ValidResult<>(false, null, error);
        }

        if (CollUtil.isNotEmpty(allowableValues) && !allowableValues.contains(fieldValue)) {
            String error = String.format(ValidConstant.VALUE_MUST_BE_IN_ENUM_LIST, annotationName, String.join(",", allowableValues));
            errorList.add(error);
            return new ValidResult<>(false, null, error);
        }

        return new ValidResult<>(true, fieldValue, null);
    }


    public ValidResult<BigDecimal> validBigDecimal(Func1<T, ?> fieldFunc) {
        return validBigDecimal(fieldFunc, null);
    }

    public ValidResult<BigDecimal> validBigDecimal(Func1<T, ?> fieldFunc, ExcelValidatorOptional optional) {
        String fieldName = LambdaUtil.getFieldName(fieldFunc);

        ExcelDataValidInfo excelDataValidInfo = excelDataValidInfoMap.get(fieldName);
        if (excelDataValidInfo == null) {
            return new ValidResult<>(true, null, null);
        }

        String fieldValue = excelDataValidInfo.getFieldValue();
        String annotationName = excelDataValidInfo.getAnnotationName();
        boolean notNull = excelDataValidInfo.isBigDecimalNotNull();
        BigDecimal less = excelDataValidInfo.getBigDecimalLess();
        BigDecimal lessOrEqual = excelDataValidInfo.getBigDecimalLessOrEqual();
        BigDecimal greater = excelDataValidInfo.getBigDecimalGreater();
        BigDecimal greaterOrEqual = excelDataValidInfo.getBigDecimalGreaterOrEqual();

        BigDecimal convertValue = null;
        try {
            if (StringUtils.isNotBlank(fieldValue)) {
                convertValue = new BigDecimal(fieldValue);
            }
        } catch (Exception e) {
            String error = String.format(ValidConstant.NUMBER_FORMAT_ERROR, annotationName);
            errorList.add(error);
            return new ValidResult<>(false, null, error);
        }

        if (Objects.nonNull(optional) && !Optional.ofNullable(optional.getValid()).orElse(true)) {
            return new ValidResult<>(true, convertValue, null);
        }

        if (StringUtils.isBlank(fieldValue)) {
            if (notNull) {
                String error = String.format(ValidConstant.FIELD_CANNOT_BE_BLANK, annotationName);
                errorList.add(error);
                return new ValidResult<>(false, null, error);
            }
            return new ValidResult<>(true, null, null);
        }

        if (less != null && convertValue.compareTo(less) >= 0) {
            String error = String.format(ValidConstant.VALUE_MUST_BE_LESS_THAN, annotationName, less);
            errorList.add(error);
            return new ValidResult<>(false, convertValue, error);
        }

        if (lessOrEqual != null && convertValue.compareTo(lessOrEqual) > 0) {
            String error = String.format(ValidConstant.VALUE_MUST_BE_LESS_OR_EQUAL, annotationName, lessOrEqual);
            errorList.add(error);
            return new ValidResult<>(false, convertValue, error);
        }

        if (greater != null && convertValue.compareTo(greater) <= 0) {
            String error = String.format(ValidConstant.VALUE_MUST_BE_GREATER_THAN, annotationName, greater);
            errorList.add(error);
            return new ValidResult<>(false, convertValue, error);
        }

        if (greaterOrEqual != null && convertValue.compareTo(greaterOrEqual) < 0) {
            String error = String.format(ValidConstant.VALUE_MUST_BE_GREATER_OR_EQUAL, annotationName, greaterOrEqual);
            errorList.add(error);
            return new ValidResult<>(false, convertValue, error);
        }

        return new ValidResult<>(true, convertValue, null);
    }

    public ValidResult<Integer> validInt(Func1<T, ?> fieldFunc) {
        return validInt(fieldFunc, null);
    }

    public ValidResult<Integer> validInt(Func1<T, ?> fieldFunc, ExcelValidatorOptional optional) {
        String fieldName = LambdaUtil.getFieldName(fieldFunc);

        ExcelDataValidInfo excelDataValidInfo = excelDataValidInfoMap.get(fieldName);
        if (excelDataValidInfo == null) {
            return new ValidResult<>(true, null, null);
        }

        String fieldValue = excelDataValidInfo.getFieldValue();
        String annotationName = excelDataValidInfo.getAnnotationName();
        boolean notNull = excelDataValidInfo.isIntNotNull();
        int less = excelDataValidInfo.getIntLess();
        int lessOrEqual = excelDataValidInfo.getIntLessOrEqual();
        int greater = excelDataValidInfo.getIntGreater();
        int greaterOrEqual = excelDataValidInfo.getIntGreaterOrEqual();

        Integer convertValue = null;
        try {
            if (StringUtils.isNotBlank(fieldValue)) {
                convertValue = Integer.parseInt(fieldValue);
            }
        } catch (Exception e) {
            String error = String.format(ValidConstant.INT_FORMAT_ERROR, annotationName);
            errorList.add(error);
            return new ValidResult<>(false, null, error);
        }

        if (Objects.nonNull(optional) && !Optional.ofNullable(optional.getValid()).orElse(true)) {
            return new ValidResult<>(true, convertValue, null);
        }

        if (StringUtils.isBlank(fieldValue)) {
            if (notNull) {
                String error = String.format(ValidConstant.FIELD_CANNOT_BE_BLANK, annotationName);
                errorList.add(error);
                return new ValidResult<>(false, null, error);
            }
            return new ValidResult<>(true, null, null);
        }

        if (convertValue.compareTo(less) >= 0) {
            String error = String.format(ValidConstant.VALUE_MUST_BE_LESS_THAN, annotationName, less);
            errorList.add(error);
            return new ValidResult<>(false, convertValue, error);
        }

        if (convertValue.compareTo(lessOrEqual) > 0) {
            String error = String.format(ValidConstant.VALUE_MUST_BE_LESS_OR_EQUAL, annotationName, lessOrEqual);
            errorList.add(error);
            return new ValidResult<>(false, convertValue, error);
        }

        if (convertValue.compareTo(greater) <= 0) {
            String error = String.format(ValidConstant.VALUE_MUST_BE_GREATER_THAN, annotationName, greater);
            errorList.add(error);
            return new ValidResult<>(false, convertValue, error);
        }

        if (convertValue.compareTo(greaterOrEqual) < 0) {
            String error = String.format(ValidConstant.VALUE_MUST_BE_GREATER_OR_EQUAL, annotationName, greaterOrEqual);
            errorList.add(error);
            return new ValidResult<>(false, convertValue, error);
        }

        return new ValidResult<>(true, convertValue, null);
    }

    public ValidResult<LocalDate> validLocalDate(Func1<T, ?> fieldFunc) {
        return validLocalDate(fieldFunc, null);
    }

    public ValidResult<LocalDate> validLocalDate(Func1<T, ?> fieldFunc, ExcelValidatorOptional optional) {
        String fieldName = LambdaUtil.getFieldName(fieldFunc);

        ExcelDataValidInfo excelDataValidInfo = excelDataValidInfoMap.get(fieldName);
        if (excelDataValidInfo == null) {
            return new ValidResult<>(true, null, null);
        }

        String fieldValue = excelDataValidInfo.getFieldValue();
        String annotationName = excelDataValidInfo.getAnnotationName();
        boolean notNull = excelDataValidInfo.isLocalDateNotNull();

        LocalDate convertValue = null;
        if (StringUtils.isNotBlank(fieldValue)) {
            List<String> datePatternList = List.of("yyyy-MM-dd", "yyyy/MM/dd", "yyyy/M/d");

            int datePatternSize = datePatternList.size();
            for (int i = 0; i < datePatternSize; i++) {
                try {
                    convertValue = LocalDate.parse(fieldValue, DateTimeFormatter.ofPattern(datePatternList.get(i)));

                    break;
                } catch (Exception e) {
                    if (i + 1 == datePatternSize) {
                        String error = String.format(ValidConstant.DATE_FORMAT_ERROR, annotationName);
                        errorList.add(error);
                        return new ValidResult<>(false, null, error);
                    }
                }
            }
        }

        if (Objects.nonNull(optional) && !Optional.ofNullable(optional.getValid()).orElse(true)) {
            return new ValidResult<>(true, convertValue, null);
        }

        if (StringUtils.isBlank(fieldValue)) {
            if (notNull) {
                String error = String.format(ValidConstant.FIELD_CANNOT_BE_BLANK, annotationName);
                errorList.add(error);
                return new ValidResult<>(false, null, error);
            }
            return new ValidResult<>(true, null, null);
        }


        return new ValidResult<>(true, convertValue, null);
    }

    public ValidResult<LocalDateTime> validLocalDateTime(Func1<T, ?> fieldFunc) {
        return validLocalDateTime(fieldFunc, null);
    }

    public ValidResult<LocalDateTime> validLocalDateTime(Func1<T, ?> fieldFunc, ExcelValidatorOptional optional) {
        String fieldName = LambdaUtil.getFieldName(fieldFunc);

        ExcelDataValidInfo excelDataValidInfo = excelDataValidInfoMap.get(fieldName);
        if (excelDataValidInfo == null) {
            return new ValidResult<>(true, null, null);
        }

        String fieldValue = excelDataValidInfo.getFieldValue();
        String annotationName = excelDataValidInfo.getAnnotationName();
        boolean notNull = excelDataValidInfo.isLocalDateTimeNotNull();

        LocalDateTime convertValue = null;
        if (StringUtils.isNotBlank(fieldValue)) {
            List<String> datePatternList = List.of("yyyy-MM-dd HH:mm:ss");

            int datePatternSize = datePatternList.size();
            for (int i = 0; i < datePatternSize; i++) {
                try {
                    convertValue = LocalDateTime.parse(fieldValue, DateTimeFormatter.ofPattern(datePatternList.get(i)));

                    break;
                } catch (Exception e) {
                    if (i + 1 == datePatternSize) {
                        String error = String.format(ValidConstant.DATE_TIME_FORMAT_ERROR, annotationName);
                        errorList.add(error);
                        return new ValidResult<>(false, null, error);
                    }
                }
            }
        }

        if (Objects.nonNull(optional) && !Optional.ofNullable(optional.getValid()).orElse(true)) {
            return new ValidResult<>(true, convertValue, null);
        }

        if (StringUtils.isBlank(fieldValue)) {
            if (notNull) {
                String error = String.format(ValidConstant.FIELD_CANNOT_BE_BLANK, annotationName);
                errorList.add(error);
                return new ValidResult<>(false, null, error);
            }
            return new ValidResult<>(true, null, null);
        }


        return new ValidResult<>(true, convertValue, null);
    }

    private void buildExcelDataValidInfoMap() {
        if (Objects.isNull(excelData)) {
            return;
        }

        Field[] fields = excelData.getClass().getDeclaredFields();
        if (fields.length == 0) {
            return;
        }

        for (Field field : fields) {
            ValidString stringAnnotation = field.getAnnotation(ValidString.class);
            ValidInt intAnnotation = field.getAnnotation(ValidInt.class);
            ValidBigDecimal bigDecimalAnnotation = field.getAnnotation(ValidBigDecimal.class);
            ValidLocalDate localDateAnnotation = field.getAnnotation(ValidLocalDate.class);
            ValidLocalDateTime localDateTimeAnnotation = field.getAnnotation(ValidLocalDateTime.class);

            // 都为空则忽略
            if ((Objects.isNull(stringAnnotation) || stringAnnotation.ignore())
                    && (Objects.isNull(intAnnotation) || intAnnotation.ignore())
                    && (Objects.isNull(bigDecimalAnnotation) || bigDecimalAnnotation.ignore())
                    && (Objects.isNull(localDateAnnotation) || localDateAnnotation.ignore())
                    && (Objects.isNull(localDateTimeAnnotation) || localDateTimeAnnotation.ignore())) {
                continue;
            }

            field.setAccessible(true);
            try {
                String fieldName = field.getName();
                // 值转化为string类型
                Object value = field.get(excelData);
                String fieldValue = value != null ? String.valueOf(value) : null;

                ExcelDataValidInfo excelDataValidInfo = new ExcelDataValidInfo();

                excelDataValidInfo.setFieldName(fieldName);
                excelDataValidInfo.setFieldValue(fieldValue);
                String annotationName = Optional.ofNullable(stringAnnotation).map(ValidString::value).orElse(null);
                if (StringUtils.isBlank(annotationName)) {
                    annotationName = Optional.ofNullable(intAnnotation).map(ValidInt::value).orElse(null);
                }
                if (StringUtils.isBlank(annotationName)) {
                    annotationName = Optional.ofNullable(bigDecimalAnnotation).map(ValidBigDecimal::value).orElse(null);
                }
                if (StringUtils.isBlank(annotationName)) {
                    annotationName = Optional.ofNullable(localDateAnnotation).map(ValidLocalDate::value).orElse(null);
                }
                if (StringUtils.isBlank(annotationName)) {
                    annotationName = Optional.ofNullable(localDateTimeAnnotation).map(ValidLocalDateTime::value).orElse(null);
                }
                if (StringUtils.isBlank(annotationName)) {
                    annotationName = fieldName;
                }
                excelDataValidInfo.setAnnotationName(annotationName);

                if (Objects.nonNull(stringAnnotation) && !stringAnnotation.ignore()) {
                    excelDataValidInfo.setStringNotBlank(stringAnnotation.notBlank());
                    excelDataValidInfo.setStringMinLength(stringAnnotation.minLength());
                    excelDataValidInfo.setStringMaxLength(stringAnnotation.maxLength());
                    String[] stringAllowableValues = stringAnnotation.allowableValues();
                    if (stringAllowableValues != null && stringAllowableValues.length > 0) {
                        excelDataValidInfo.setStringAllowableValues(Arrays.asList(stringAllowableValues));
                    }
                }

                if (Objects.nonNull(intAnnotation) && !intAnnotation.ignore()) {
                    excelDataValidInfo.setIntNotNull(intAnnotation.notNull());
                    excelDataValidInfo.setIntLess(intAnnotation.less());
                    excelDataValidInfo.setIntLessOrEqual(intAnnotation.lessOrEqual());
                    excelDataValidInfo.setIntGreater(intAnnotation.greater());
                    excelDataValidInfo.setIntGreaterOrEqual(intAnnotation.greaterOrEqual());
                }

                if (Objects.nonNull(bigDecimalAnnotation) && !bigDecimalAnnotation.ignore()) {
                    excelDataValidInfo.setBigDecimalNotNull(bigDecimalAnnotation.notNull());
                    String less = bigDecimalAnnotation.less();
                    if (StringUtils.isNotBlank(less)) {
                        excelDataValidInfo.setBigDecimalLess(new BigDecimal(less));
                    }
                    String lessOrEqual = bigDecimalAnnotation.lessOrEqual();
                    if (StringUtils.isNotBlank(lessOrEqual)) {
                        excelDataValidInfo.setBigDecimalLessOrEqual(new BigDecimal(lessOrEqual));
                    }
                    String greater = bigDecimalAnnotation.greater();
                    if (StringUtils.isNotBlank(greater)) {
                        excelDataValidInfo.setBigDecimalGreater(new BigDecimal(greater));
                    }
                    String greaterOrEqual = bigDecimalAnnotation.greaterOrEqual();
                    if (StringUtils.isNotBlank(greaterOrEqual)) {
                        excelDataValidInfo.setBigDecimalGreaterOrEqual(new BigDecimal(greaterOrEqual));
                    }
                }

                if (Objects.nonNull(localDateAnnotation) && !localDateAnnotation.ignore()) {
                    excelDataValidInfo.setLocalDateNotNull(localDateAnnotation.notNull());
                }

                if (Objects.nonNull(localDateTimeAnnotation) && !localDateTimeAnnotation.ignore()) {
                    excelDataValidInfo.setLocalDateTimeNotNull(localDateTimeAnnotation.notNull());
                }

                excelDataValidInfoMap.put(fieldName, excelDataValidInfo);
            } catch (IllegalArgumentException | IllegalAccessException ignored) {
            }
        }
    }
}

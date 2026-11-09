package com.wd.common.excel.valid;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

/**
 *
 *
 * @author huangwenda
 * @date 2025/10/31 16:30
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidResult<T> {

    @Schema(description = "是否验证通过")
    private Boolean success;

    @Schema(description = "解析之后的值")
    private T value;

    @Schema(description = "错误信息")
    private String error;

    public boolean validSuccess() {
        return Optional.ofNullable(success).orElse(false);
    }

    public boolean validFail() {
        return !validSuccess();
    }
}

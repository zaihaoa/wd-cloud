package com.wd.common.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author: ccd/Cheng
 * @create: 2024-08-08 09:28
 * @Description:
 */
@Schema(description = "导入参数")
@Getter
@Setter
@ToString
public class ImportParam {

    @Schema(description = "文件路径", example = "/te/sd/123.xlsx")
    @NotBlank(message = "文件路径不能为空")
    private String filePath;

}

package com.wd.common.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "导出结果")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ExportResult {

    @Schema(description = "文件名称", example = "123.png")
    private String fileName;

    @Schema(description = "文件路径", example = "/te/sd/123.xlsx")
    private String filePath;
}

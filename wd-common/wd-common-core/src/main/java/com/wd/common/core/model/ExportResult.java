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

    @Schema(description = "文件名称")
    private String fileName;

    @Schema(description = "文件链接")
    private String fileLink;
}

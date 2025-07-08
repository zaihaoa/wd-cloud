package com.wd.common.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "导入结果")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportResult<T> {

    @Schema(description = "是否导入成功")
    private Boolean importSuccess;

    @Schema(description = "总记录数")
    private Integer totalCount;

    @Schema(description = "成功记录数")
    private Integer successCount;

    @Schema(description = "错误记录数")
    private Integer errorCount;

    @Schema(description = "错误文件名称")
    private String errorFileName;

    @Schema(description = "错误文件路径")
    private String errorFilePath;

    @Schema(description = "数据")
    private T data;
}

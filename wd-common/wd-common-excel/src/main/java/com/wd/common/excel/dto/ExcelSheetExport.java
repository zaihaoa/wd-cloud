package com.wd.common.excel.dto;

import com.wd.common.core.model.BaseExportQuery;
import com.wd.common.excel.handler.CellSelector;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.function.Function;

/**
 *
 *
 * @author huangwenda
 * @date 2025/12/22 11:46
 **/
@Schema(description = "ExcelSheet导出参数")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcelSheetExport<T extends BaseExportQuery, R> {

    @Schema(description = "sheet名称")
    private String sheetName;

    @Schema(description = "分页参数")
    private T pageParam;

    @Schema(description = "分页统计函数")
    private Function<T, Integer> pageCountFunction;

    @Schema(description = "Excel数据函数")
    private Function<T, List<R>> excelDataFunction;

    @Schema(description = "Excel数据类")
    private Class<R> excelClass;

    @Schema(description = "需要排除的列字段名")
    private List<String> excludeFields;

    @Schema(description = "列选择器")
    private List<CellSelector> cellSelectors;
}

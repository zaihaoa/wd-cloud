package com.wd.common.core;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author huangwenda
 * @date 2022/11/1 14:16
 **/
@Getter
@Setter
@ToString
@Schema(description = "基础导出分页查询条件")
public abstract class BaseExportQuery<T> extends BaseQuery {

    @Schema(description = "勾选项主键", example = "[1,2,3]")
    protected List<T> exportKeys;

    @Schema(description = "导出方式(ExportTypeEnum)", example = "QUERY_RESULT", allowableValues = {"SELECTED", "CURRENT_PAGE", "QUERY_RESULT"})
    protected String exportType;
}

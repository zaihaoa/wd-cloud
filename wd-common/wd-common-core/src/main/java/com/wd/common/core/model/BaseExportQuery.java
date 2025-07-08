package com.wd.common.core.model;

import com.wd.common.core.enums.ExportTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author huangwenda
 * @date 2022/11/1 14:16
 **/
@Schema(description = "基础导出分页查询条件")
@Getter
@Setter
@ToString
public abstract class BaseExportQuery<T> extends BaseQuery {

    @Schema(description = "勾选项主键", example = "[1,2,3]")
    protected List<T> exportKeys;

    /**
     * {@link ExportTypeEnum}
     */
    @Schema(description = "导出方式(SELECTED:导出勾选项,CURRENT_PAGE:导出当前页,QUERY_RESULT:导出查询结果)", example = "QUERY_RESULT", allowableValues = {"SELECTED", "CURRENT_PAGE", "QUERY_RESULT"})
    protected String exportType;
}

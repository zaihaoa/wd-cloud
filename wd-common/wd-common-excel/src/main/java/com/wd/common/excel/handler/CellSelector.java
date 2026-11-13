package com.wd.common.excel.handler;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 配置 Excel 导出字段的下拉选项。
 *
 * @author huangwenda
 * @date 2025/6/21 19:29
 **/
@Schema(description = "单元格下拉")
@Getter
@Setter
@ToString
public class CellSelector {

    /**
     * Excel 导出类中的 Java 字段名。字段未导出时跳过该配置。
     */
    private String fieldName;

    /**
     * 下拉选项的数据
     */
    private List<String> selectDataList;

    public CellSelector(String fieldName, List<String> selectDataList) {
        this.fieldName = fieldName;
        this.selectDataList = selectDataList;
    }
}

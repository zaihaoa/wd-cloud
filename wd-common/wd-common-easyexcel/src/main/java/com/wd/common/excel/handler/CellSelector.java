package com.wd.common.excel.handler;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 *
 * 构造下拉选项单元格列的位置，以及下拉选项可选参数值的map集合
 * columnIndex：下拉选项要放到哪个单元格，比如A列的单元格那就是0，C列的单元格，那就是2
 * selectDataList：columnIndex对应的那个单元格下拉列表里的数据项，比如这里就是下拉选项1..100
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
     * 下拉选项要放到哪个单元格，比如A列的单元格那就是0，C列的单元格，那就是2
     */
    private Integer columnIndex;

    /**
     * 下拉选项的数据
     */
    private List<String> selectDataList;

    public CellSelector(Integer columnIndex, List<String> selectDataList) {
        this.columnIndex = columnIndex;
        this.selectDataList = selectDataList;
    }
}

package com.wd.common.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author huangwenda
 * @date 2023/3/24 13:54
 **/
@Getter
@AllArgsConstructor
public enum ExportTypeEnum implements CodeDescEnum<String> {
    SELECTED("SELECTED", "导出勾选项"),
    CURRENT_PAGE("CURRENT_PAGE", "导出当前页"),
    QUERY_RESULT("QUERY_RESULT", "导出查询结果");

    private final String code;

    private final String desc;
}

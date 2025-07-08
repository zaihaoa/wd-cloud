package com.wd.common.core.util;



import com.wd.common.core.enums.ExportTypeEnum;
import com.wd.common.core.model.BaseExportQuery;

import java.util.List;

/**
 * @author huangwenda
 * @date 2023/9/13 17:37
 **/
public class ExportUtil {
    /**
     * 导出默认分页大小
     */
    public static final int EXPORT_DEFAULT_PAGE_SIZE = 2000;


    public static void pageExportParamValidAndDefaultValue(BaseExportQuery exportQuery) {
        pageExportParamValidAndDefaultValue(exportQuery, EXPORT_DEFAULT_PAGE_SIZE);
    }

    /**
     * 导出参数校验及赋默认值
     *
     * @param exportQuery 导出参数
     * @param size        每次查询分页大小
     */
    public static void pageExportParamValidAndDefaultValue(BaseExportQuery exportQuery, int size) {
        String exportType = exportQuery.getExportType();
        if (!CodeDescEnumUtil.isExist(ExportTypeEnum.class, exportType)) {
            throw new IllegalArgumentException("导出类型不正确");
        }

        if (ExportTypeEnum.SELECTED.getCode().equals(exportType)) {
            List exportKeys = exportQuery.getExportKeys();
            if (exportKeys == null || exportKeys.isEmpty()) {
                throw new IllegalArgumentException("请勾选需要导出记录");
            }

            exportQuery.setCurrent(1);
            exportQuery.setSize(exportKeys.size());
        } else if (ExportTypeEnum.CURRENT_PAGE.getCode().equals(exportType)) {
            exportQuery.setExportKeys(null);
        } else if (ExportTypeEnum.QUERY_RESULT.getCode().equals(exportType)) {
            exportQuery.setExportKeys(null);

            exportQuery.setCurrent(1);
            exportQuery.setSize(size);
        }
    }
}

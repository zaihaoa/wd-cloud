package com.wd.common.core.exception;

/**
 * excel导出异常
 *
 * @author huangwenda
 * @date 2023/2/16 18:58
 **/
public class ExcelExportException extends RuntimeException {

    public ExcelExportException() {
    }

    public ExcelExportException(String message) {
        super(message);
    }
}

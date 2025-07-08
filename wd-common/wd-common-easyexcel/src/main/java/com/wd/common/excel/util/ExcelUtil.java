package com.wd.common.excel.util;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author huangwenda
 * @date 2023/8/18 14:39
 **/
public class ExcelUtil {

    public static void initResponseHeader(String fileName, HttpServletResponse response) {
        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setHeader("filename", fileName);

        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
    }

    @SneakyThrows
    public static ServletOutputStream getOutputStream(HttpServletResponse response) {
        return response.getOutputStream();
    }

}

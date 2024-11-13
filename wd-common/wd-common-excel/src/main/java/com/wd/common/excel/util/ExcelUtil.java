package com.wd.common.excel.util;

import com.alibaba.excel.ExcelWriter;
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

    public static void writeResponse(String fileName, HttpServletResponse response, ExcelWriter excelWriter) {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String encode = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        response.setHeader("Content-disposition", "attachment;filename=" + encode + ".xlsx");

        excelWriter.finish();
    }

    @SneakyThrows
    public static ServletOutputStream getOutputStream(HttpServletResponse response) {
        return response.getOutputStream();
    }

}

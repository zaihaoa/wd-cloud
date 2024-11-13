package com.wd.system.system.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author huangwenda
 * @date 2023/3/24 14:50
 **/
@Getter
@Setter
@ToString
@HeadRowHeight(20)
@ContentRowHeight(20)
@HeadFontStyle(fontHeightInPoints = 12, fontName = "等线 (正文)")
@ContentFontStyle(fontHeightInPoints = 12, fontName = "等线 (正文)")
public class UserExportDTO {

    @ExcelProperty(value = "登录名")
    @ColumnWidth(20)
    private String loginName;

    @ExcelProperty(value = "用户姓名")
    @ColumnWidth(20)
    private String realName;

    @ExcelProperty(value = "状态")
    @ColumnWidth(15)
    private String statusDesc;

    @ExcelProperty(value = "类型")
    @ColumnWidth(15)
    private String typeDesc;

    @ExcelProperty(value = "性别")
    @ColumnWidth(10)
    private String sexDesc;

    @ExcelProperty(value = "手机号码")
    @ColumnWidth(20)
    private String tel;

    @ExcelProperty(value = "邮箱地址")
    @ColumnWidth(20)
    private String email;

    @ExcelProperty(value = "创建时间")
    @ColumnWidth(20)
    private LocalDateTime createTime;

    @ExcelProperty(value = "更新时间")
    @ColumnWidth(20)
    private LocalDateTime updateTime;
}

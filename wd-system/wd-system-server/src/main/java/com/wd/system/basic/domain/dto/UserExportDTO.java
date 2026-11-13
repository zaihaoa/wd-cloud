package com.wd.system.basic.domain.dto;

import com.wd.common.core.annotions.FieldPermission;
import com.wd.common.core.constants.FieldPermissionConstant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.write.style.*;
import org.apache.fesod.sheet.enums.BooleanEnum;
import org.apache.fesod.sheet.enums.poi.FillPatternTypeEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@HeadRowHeight(40)
@ContentRowHeight(20)
@HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 30)
@HeadFontStyle(fontHeightInPoints = 12, fontName = "微软雅黑", bold = BooleanEnum.FALSE, color = 1)
@ContentFontStyle(fontHeightInPoints = 12, fontName = "微软雅黑 Light")
public class UserExportDTO {

    @ExcelProperty("用户名")
    @ColumnWidth(20)
    private String username;

    @ExcelProperty("姓名")
    @ColumnWidth(20)
    private String name;

    @ExcelProperty("状态")
    @ColumnWidth(20)
    private String statusDesc;

    @ExcelProperty("类型")
    @ColumnWidth(20)
    private String typeDesc;

    @ExcelProperty("手机号码")
    @ColumnWidth(20)
    @FieldPermission(FieldPermissionConstant.USER_PHONE)
    private String phone;

    @ExcelProperty("邮箱")
    @ColumnWidth(20)
    private String email;

    @ExcelProperty("角色")
    @ColumnWidth(20)
    private String roleNameJoin;

    @ExcelProperty("创建人")
    @ColumnWidth(20)
    private String createUserName;

    @ExcelProperty("更新人")
    @ColumnWidth(20)
    private String updateUserName;

    @ExcelProperty("创建时间")
    @ColumnWidth(20)
    private LocalDateTime createTime;

    @ExcelProperty("更新时间")
    @ColumnWidth(20)
    private LocalDateTime updateTime;

}

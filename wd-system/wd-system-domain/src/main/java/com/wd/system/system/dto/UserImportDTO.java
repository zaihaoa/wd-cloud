package com.wd.system.system.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author huangwenda
 * @date 2023/3/24 14:50
 **/
@Getter
@Setter
@ToString
public class UserImportDTO {

    @ExcelProperty(value = "登录名(必填)")
    private String loginName;

    @ExcelProperty(value = "姓名(必填)")
    private String realName;

    @ExcelProperty(value = "类型(必填)")
    private String type;

    @ExcelProperty(value = "性别")
    private String sex;

    @ExcelProperty(value = "手机号码")
    private String tel;

    @ExcelProperty(value = "邮箱地址")
    private String email;
}

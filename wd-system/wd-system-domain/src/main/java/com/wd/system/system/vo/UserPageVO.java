package com.wd.system.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author huangwenda
 * @date 2023/1/30 11:38
 **/
@Getter
@Setter
@ToString
@Schema(description = "用户列表返回")
public class UserPageVO {

    @Schema(description = "ID", example = "12345678910")
    private Long id;

    @Schema(description = "登录名", example = "张三")
    private String loginName;

    @Schema(description = "用户姓名", example = "张三")
    private String realName;

    @Schema(description = "状态编码", example = "ENABLE")
    private String status;

    @Schema(description = "状态", example = "启用")
    private String statusDesc;

    @Schema(description = "类型编码", example = "NORMAL")
    private String type;

    @Schema(description = "类型", example = "普通用户")
    private String typeDesc;

    @Schema(description = "性别编码", example = "MEN")
    private String sex;

    @Schema(description = "性别", example = "男")
    private String sexDesc;

    @Schema(description = "手机号码", example = "13798334509")
    private String tel;

    @Schema(description = "邮箱地址", example = "qqqq@qq.com")
    private String email;

    @Schema(description = "创建时间", example = "2022-01-01 11:12:13")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2022-01-01 11:12:13")
    private LocalDateTime updateTime;
}

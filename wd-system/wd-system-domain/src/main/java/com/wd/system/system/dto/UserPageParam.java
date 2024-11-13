package com.wd.system.system.dto;

import com.wd.common.core.BaseExportQuery;
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
@Schema(name = "UserPageParam", description = "用户列表查询条件")
public class UserPageParam extends BaseExportQuery<Long> {

    @Schema(description = "用户名", example = "ZhangSan")
    private String loginName;

    @Schema(description = "用户姓名", example = "张三")
    private String realName;

    @Schema(description = "状态", example = "ENABLE")
    private String status;

    @Schema(description = "性别", example = "MEN")
    private String sex;

    @Schema(description = "手机号码", example = "13798334509")
    private String tel;

    @Schema(description = "邮箱地址", example = "qqqq@qq.com")
    private String email;

    @Schema(description = "创建时间-开始", example = "2023-01-01 00:00:00")
    private LocalDateTime createTimeBegin;

    @Schema(description = "创建时间-结束", example = "2023-01-01 23:59:59")
    private LocalDateTime createTimeEnd;

    @Schema(description = "更新时间-开始", example = "2023-01-01 00:00:00")
    private LocalDateTime updateTimeBegin;

    @Schema(description = "更新时间-结束", example = "2023-01-01 23:59:59")
    private LocalDateTime updateTimeEnd;
}

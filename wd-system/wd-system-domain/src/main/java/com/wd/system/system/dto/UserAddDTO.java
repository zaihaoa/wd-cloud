package com.wd.system.system.dto;

import com.wd.common.core.annotions.EnumCodeExist;
import com.wd.system.system.enums.UserSexEnum;
import com.wd.system.system.enums.UserTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author huangwenda
 * @date 2023/1/30 11:38
 **/
@Getter
@Setter
@ToString
@Schema(description = "添加用户参数")
public class UserAddDTO {

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "ZhangSan")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 1, max = 64, message = "用户名长度范围[1,64]")
    private String loginName;

    @Schema(description = "用户姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "用户姓名不能为空")
    @Size(min = 1, max = 64, message = "用户姓名长度范围[1,64]")
    private String realName;

    @Schema(description = "用户密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "类型(NORMAL:普通,MANAGER:普通管理员)", requiredMode = Schema.RequiredMode.REQUIRED, example = "NORMAL")
    @EnumCodeExist(message = "类型填写不正确", enumClass = UserTypeEnum.class)
    private String type;

    @Schema(description = "头像图片路径", example = "https://cf.shopee.sg/file/3f3d7d242fb9da90b99e785f8f108fa9_tn")
    @Size(max = 256, message = "图片地址URL不能超过256")
    private String avatarImageUrl;

    @Schema(description = "性别", example = "MAN")
    @EnumCodeExist(message = "性别填写不正确", enumClass = UserSexEnum.class)
    private String sex;

    @Schema(description = "手机号码", example = "13798334509")
    @Size(max = 64, message = "手机号码不能超过128字数")
    private String tel;

    @Schema(description = "邮箱地址", example = "qqqq@qq.com")
    @Size(max = 128, message = "邮箱地址不能超过128字数")
    private String email;
}

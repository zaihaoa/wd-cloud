package com.wd.system.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wd.common.core.BaseUserTimeEntity;
import com.wd.system.system.enums.UserSexEnum;
import com.wd.system.system.enums.UserStatusEnum;
import com.wd.system.system.enums.UserTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 用户
 * </p>
 *
 * @author huangwenda
 * @since 2023-01-30
 */
@Getter
@Setter
@ToString
@TableName("sys_user")
public class User extends BaseUserTimeEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.INPUT)
    private Long id;

    /**
     * 登录名
     */
    private String loginName;

    /**
     * 用户姓名
     */
    private String realName;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 状态
     * {@link UserStatusEnum}
     */
    private String status;

    /**
     * 类型(NORMAL:普通,MANAGER:管理员,SYSTEM_MANAGER:系统管理员,SYSTEM_SUPER_ADMIN:系统超级管理员)
     * {@link UserTypeEnum}
     */
    private String type;

    /**
     * 头像图片路径
     */
    private String avatarImageUrl;

    /**
     * 性别
     * {@link UserSexEnum}
     */
    private String sex;

    /**
     * 手机号码
     */
    private String tel;

    /**
     * 邮箱地址
     */
    private String email;
}

package com.wd.system.basic.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wd.common.core.model.BaseUserTimeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * 用户配置
 * </p>
 *
 * @author huangwenda
 * @since 2026-11-09
 */
@Getter
@Setter
@ToString
@TableName("sys_user_config")
@Schema(name = "UserConfig", description = "用户配置")
public class UserConfig extends BaseUserTimeEntity {

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;

    @TableField("type")
    @Schema(description = "类型(CUSTOM_COLUMN:自定义列,QUERY:查询条件)")
    private String type;

    @TableField("code")
    @Schema(description = "编码")
    private String code;

    @TableField("content")
    @Schema(description = "内容")
    private String content;
}

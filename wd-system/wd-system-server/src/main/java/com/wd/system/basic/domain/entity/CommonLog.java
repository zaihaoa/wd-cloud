package com.wd.system.basic.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wd.common.core.model.BaseUserTimeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 通用日志
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@Getter
@Setter
@TableName("sys_common_log")
@Schema(name = "CommonLog", description = "通用日志")
public class CommonLog extends BaseUserTimeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "业务类型")
    @TableField("business_type")
    private String businessType;

    @Schema(description = "业务ID")
    @TableField("business_id")
    private Long businessId;

    @Schema(description = "操作类型")
    @TableField("operation_type")
    private String operationType;

    @Schema(description = "操作内容")
    @TableField("content")
    private String content;
}

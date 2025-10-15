package com.wd.system.basic.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wd.common.core.model.BaseUserTimeEntity;
import java.io.Serializable;

import com.wd.system.basic.domain.enums.GlobalConfigTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 全局配置
 * </p>
 *
 * @author huangwenda
 * @since 2025-10-11
 */
@Getter
@Setter
@TableName("sys_global_config")
@Schema(name = "GlobalConfig", description = "全局配置")
public class GlobalConfig extends BaseUserTimeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "编码")
    @TableField("code")
    private String code;

    @Schema(description = "值")
    @TableField("value")
    private String value;

    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

    /**
     * {@link GlobalConfigTypeEnum}
     */
    @Schema(description = "类型(SYSTEM:系统内置,NORMAL:普通)")
    @TableField("type")
    private String type;

    @Schema(description = "显示顺序，越小越优先")
    @TableField("sort_number")
    private Integer sortNumber;

    @Schema(description = "删除标记，等于0表示没有删除，否则为删除")
    @TableField("delete_flag")
    private Long deleteFlag;
}

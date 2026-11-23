package com.wd.system.basic.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wd.common.core.model.BaseUserTimeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName(value = "sys_dictionary", autoResultMap = true)
@Schema(description = "字典")
public class Dictionary extends BaseUserTimeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "字典编码")
    @TableField("code")
    private String code;

    @Schema(description = "描述")
    @TableField("`desc`")
    private String desc;

    @Schema(description = "状态")
    @TableField("status")
    private String status;

    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

    @Schema(description = "逻辑删除标记，0表示有效，删除后为记录ID")
    @TableField("delete_flag")
    @TableLogic(value = "0")
    private Long deleteFlag;
}

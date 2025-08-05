package com.wd.common.core.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.wd.common.core.constants.TableConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author huangwenda
 * @date 2023/1/30 11:05
 **/
@Getter
@Setter
@ToString
public class BaseUserTimeEntity {

    @Schema(description = "创建人ID")
    @TableField(value = TableConstant.TABLE_CREATE_USER_ID, fill = FieldFill.INSERT, insertStrategy = FieldStrategy.DEFAULT, updateStrategy = FieldStrategy.NEVER)
    protected Long createUserId;

    @Schema(description = "更新人ID")
    @TableField(value = TableConstant.TABLE_UPDATE_USER_ID, fill = FieldFill.INSERT_UPDATE, insertStrategy = FieldStrategy.DEFAULT, updateStrategy = FieldStrategy.DEFAULT)
    protected Long updateUserId;

    @Schema(description = "创建时间")
    @TableField(value = TableConstant.TABLE_CREATE_TIME, fill = FieldFill.DEFAULT, insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    protected LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(value = TableConstant.TABLE_UPDATE_TIME, fill = FieldFill.DEFAULT, insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    protected LocalDateTime updateTime;
}

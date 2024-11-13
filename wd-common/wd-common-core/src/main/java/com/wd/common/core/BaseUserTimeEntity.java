package com.wd.common.core;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author huangwenda
 * @date 2023/1/30 11:05
 **/
@Getter
@Setter
@ToString
public class BaseUserTimeEntity extends BaseTimeEntity {
    /**
     * 创建人用户ID
     */
    @TableField(value = "create_user_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    protected Long createUserId;

    /**
     * 更新人用户ID
     */
    @TableField(value = "update_user_id", fill = FieldFill.INSERT_UPDATE)
    protected Long updateUserId;
}

package com.wd.common.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author huangwenda
 * @date 2022/11/1 14:16
 **/
@Schema(description = "基础分页查询条件")
@Getter
@Setter
@ToString
public abstract class BaseQuery {

    @Schema(description = "页码", example = "1")
    @Positive(message = "页码必须为正数")
    protected int current = 1;

    @Schema(description = "每页显示多少条", example = "10")
    @Positive(message = "每页条数必须为正数")
    protected int size = 10;

    @Schema(description = "排序字段", example = "id")
    protected String sort;

    /**
     * PageUtil.ASC
     * PageUtil.DESC
     */
    @Schema(description = "排序规则,默认ASC（ASC:升序,DESC:降序）", example = "ASC", allowableValues = {"ASC", "DESC"})
    @Pattern(regexp = "ASC|DESC", message = "排序规则必须为ASC或DESC")
    protected String order = "ASC";
}

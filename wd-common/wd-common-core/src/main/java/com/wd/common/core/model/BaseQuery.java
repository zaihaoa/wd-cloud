package com.wd.common.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "第几页", example = "1")
    protected int current = 1;

    @Schema(description = "每页显示多少条", example = "10")
    protected int size = 10;

    @Schema(description = "排序字段", example = "id")
    protected String sort;

    /**
     * PageUtil.ASC
     */
    @Schema(description = "排序规则,默认ASC（ASC:升序,DESC:降序）", example = "ASC", allowableValues = {"ASC", "DESC"})
    protected String order = "ASC";
}

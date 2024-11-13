package com.wd.common.core;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author huangwenda
 * @date 2022/11/1 14:16
 **/
@Getter
@Setter
@ToString
@Schema(description = "基础分页查询条件")
public abstract class BaseQuery {

    @Schema(description = "第几页", example = "1")
    protected long current = 1L;

    @Schema(description = "每页显示多少条", example = "10")
    protected long size = 10L;

    @Schema(description = "排序字段", example = "id")
    protected String sort;

    @Schema(description = "排序规则(默认ASC)", example = "ASC", allowableValues = {"ASC", "DESC"})
    protected String order = "ASC";
}

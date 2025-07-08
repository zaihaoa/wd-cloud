package com.wd.common.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * @author huangwenda
 * @date 2023/1/30 15:10
 **/
@Schema(description = "分页返回")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageInfo<T> {

    @Schema(description = "当前页,默认:1", example = "1")
    private int current = 1;

    @Schema(description = "每页显示条数,默认:10", example = "10")
    private int size = 10;

    @Schema(description = "总数", example = "100")
    private int total = 0;

    @Schema(description = "数据")
    private List<T> records;
}

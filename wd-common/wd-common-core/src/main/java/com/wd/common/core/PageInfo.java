package com.wd.common.core;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * @author huangwenda
 * @date 2023/1/30 15:10
 **/
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "分页返回")
public class PageInfo<T> {
    @Schema(description = "当前页,默认:1", example = "1")
    private long current = 1L;

    @Schema(description = "每页显示条数,默认:10", example = "10")
    private long size = 10L;

    @Schema(description = "总数", example = "100")
    private long total = 0L;

    @Schema(description = "数据")
    private List<T> records;
}

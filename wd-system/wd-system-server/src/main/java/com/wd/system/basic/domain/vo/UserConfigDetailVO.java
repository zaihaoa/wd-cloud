package com.wd.system.basic.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 *
 *
 * @author huangwenda
 * @date 2025/9/24 17:50
 **/
@Getter
@Setter
public class UserConfigDetailVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "类型(CUSTOM_COLUMN:自定义列,QUERY:查询条件)")
    private String type;

    @Schema(description = "类型描述(CUSTOM_COLUMN:自定义列,QUERY:查询条件)")
    private String typeDesc;

    @Schema(description = "编码")
    private String code;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "创建人用户ID")
    private Long createUserId;

    @Schema(description = "更新人用户ID")
    private Long updateUserId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

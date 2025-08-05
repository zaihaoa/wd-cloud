package com.wd.system.basic.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <p>
 * 通用日志分页DTO
 * </p>
 *
 * @author huangwenda
 * @since 2025-07-08
 */
@Getter
@Setter
@Schema(description = "通用日志列表DTO")
public class CommonLogPageDTO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "业务类型")
    private String businessType;

    @Schema(description = "业务ID")
    private Long businessId;

    @Schema(description = "操作类型")
    private String operationType;

    @Schema(description = "操作内容")
    private String content;

    @Schema(description = "创建人ID")
    private Long createUserId;

    @Schema(description = "更新人ID")
    private Long updateUserId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

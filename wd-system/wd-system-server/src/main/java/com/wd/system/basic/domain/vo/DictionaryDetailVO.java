package com.wd.system.basic.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "字典详情")
public class DictionaryDetailVO {
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "字典编码")
    private String code;

    @Schema(description = "描述")
    private String desc;

    @Schema(description = "状态", allowableValues = {"ENABLED", "DISABLED"})
    private String status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建人ID")
    private Long createUserId;

    @Schema(description = "更新人ID")
    private Long updateUserId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}

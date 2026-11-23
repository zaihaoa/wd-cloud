package com.wd.system.basic.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "字典明细分页查询结果")
public class DictionaryItemPageDTO {
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "所属字典编码")
    private String dictionaryCode;

    @Schema(description = "明细编码")
    private String itemCode;

    @Schema(description = "明细描述")
    private String itemDesc;

    @Schema(description = "显示顺序，越小越优先")
    private Integer sortNumber;

    @Schema(description = "状态", allowableValues = {"ENABLED", "DISABLED"})
    private String status;

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

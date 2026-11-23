package com.wd.system.basic.domain.dto;

import com.wd.common.core.model.BaseQuery;
import com.wd.system.basic.domain.enums.DictionaryStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "字典明细分页查询参数")
public class DictionaryItemPageParamDTO extends BaseQuery {

    @Schema(description = "所属字典编码")
    @Size(max = 200, message = "所属字典编码不能超过200个字符")
    private String dictionaryCode;

    @Schema(description = "明细编码")
    @Size(max = 200, message = "明细编码不能超过200个字符")
    private String itemCode;

    @Schema(description = "明细描述")
    private String itemDesc;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态", allowableValues = {"ENABLED", "DISABLED"})
    @Pattern(regexp = DictionaryStatusEnum.CODE_PATTERN, message = "状态必须为ENABLED或DISABLED")
    private String status;
}

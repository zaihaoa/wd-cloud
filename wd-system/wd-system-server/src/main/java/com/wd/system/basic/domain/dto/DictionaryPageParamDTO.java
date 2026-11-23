package com.wd.system.basic.domain.dto;

import com.wd.common.core.model.BaseQuery;
import com.wd.system.basic.domain.enums.DictionaryStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "字典分页查询参数")
public class DictionaryPageParamDTO extends BaseQuery {

    @Schema(description = "字典编码")
    private String code;

    @Schema(description = "字典描述")
    private String desc;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态", allowableValues = {"ENABLED", "DISABLED"})
    @Pattern(regexp = DictionaryStatusEnum.CODE_PATTERN, message = "状态必须为ENABLED或DISABLED")
    private String status;
}

package com.wd.system.basic.domain.dto;

import com.wd.common.core.objectcompare.ObjectCompare;
import com.wd.system.basic.domain.enums.DictionaryStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "字典明细覆盖编辑参数")
public class DictionaryItemUpdateDTO {
    @Schema(description = "主键ID")
    @NotNull(message = "主键ID不能为空")
    @Positive(message = "主键ID必须为正数")
    private Long id;

    @Schema(description = "明细描述")
    @NotBlank(message = "明细描述不能为空")
    @Size(max = 2000, message = "明细描述不能超过2000个字符")
    @ObjectCompare("字典明细描述")
    private String itemDesc;

    @Schema(description = "显示顺序，越小越优先", defaultValue = "0")
    @ObjectCompare("排序")
    private Integer sortNumber;

    @Schema(description = "状态", allowableValues = {"ENABLED", "DISABLED"})
    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = DictionaryStatusEnum.CODE_PATTERN, message = "状态必须为ENABLED或DISABLED")
    @ObjectCompare("状态")
    private String status;

    @Schema(description = "备注")
    @Size(max = 2000, message = "备注不能超过2000个字符")
    @ObjectCompare("备注")
    private String remark;

}

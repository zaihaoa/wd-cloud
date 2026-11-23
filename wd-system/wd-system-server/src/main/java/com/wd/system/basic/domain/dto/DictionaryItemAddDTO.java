package com.wd.system.basic.domain.dto;

import com.wd.system.basic.domain.enums.DictionaryStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "字典明细新增参数")
public class DictionaryItemAddDTO {
    @Schema(description = "所属字典编码")
    @NotBlank(message = "所属字典编码不能为空")
    @Size(max = 200, message = "所属字典编码不能超过200个字符")
    private String dictionaryCode;

    @Schema(description = "明细编码")
    @NotBlank(message = "明细编码不能为空")
    @Size(max = 200, message = "明细编码不能超过200个字符")
    private String itemCode;

    @Schema(description = "明细描述")
    @NotBlank(message = "明细描述不能为空")
    @Size(max = 2000, message = "明细描述不能超过2000个字符")
    private String itemDesc;

    @Schema(description = "显示顺序，越小越优先")
    private Integer sortNumber;

    @Schema(description = "状态", allowableValues = {"ENABLED", "DISABLED"})
    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = DictionaryStatusEnum.CODE_PATTERN, message = "状态必须为ENABLED或DISABLED")
    private String status;

    @Schema(description = "备注")
    @Size(max = 2000, message = "备注不能超过2000个字符")
    private String remark;

}

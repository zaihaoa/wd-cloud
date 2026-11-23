package com.wd.system.basic.domain.dto;

import com.wd.system.basic.domain.enums.DictionaryStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "字典或字典明细批量状态更新参数")
public class DictionaryBatchUpdateStatusDTO {

    @Schema(description = "字典或字典明细 ID 列表", example = "[123,124]")
    @NotEmpty(message = "ID列表不能为空")
    private List<@NotNull(message = "ID不能为空") @Positive(message = "ID必须为正数") Long> ids;

    @Schema(description = "目标状态", allowableValues = {"ENABLED", "DISABLED"})
    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = DictionaryStatusEnum.CODE_PATTERN, message = "状态必须为ENABLED或DISABLED")
    private String status;
}

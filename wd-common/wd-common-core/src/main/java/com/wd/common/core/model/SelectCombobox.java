package com.wd.common.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "下拉选择")
public class SelectCombobox {

    @Schema(description = "键", example = "key1")
    private String value;

    @Schema(description = "值", example = "选项1")
    private String label;

}

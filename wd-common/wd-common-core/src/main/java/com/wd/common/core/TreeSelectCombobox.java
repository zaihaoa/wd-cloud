package com.wd.common.core;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * 孩子们
 *
 * @author huangwenda
 * @date 2023/8/31 11:19
 **/
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Tree下拉选择")
public class TreeSelectCombobox {

    @Schema(description = "键", example = "key1")
    private String key;

    @Schema(description = "值", example = "选项1")
    private String value;

    @Schema(description = "子节点")
    private List<TreeSelectCombobox> children;
}

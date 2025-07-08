package com.wd.common.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Schema(description = "Tree下拉选择")
public class TreeSelectCombobox {

    @Schema(description = "键", example = "key1")
    private String value;

    @Schema(description = "值", example = "选项1")
    private String label;

    @Schema(description = "子节点")
    private List<TreeSelectCombobox> children;

    public TreeSelectCombobox() {
    }

    public TreeSelectCombobox(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public TreeSelectCombobox(String value, String label, List<TreeSelectCombobox> children) {
        this.value = value;
        this.label = label;
        this.children = children;
    }
}

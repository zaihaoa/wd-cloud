package com.wd.common.core.objectcompare;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author huangwenda
 * @date 2023/3/8 14:17
 **/
@Getter
@Setter
@ToString
public class ObjectCompareResult {

    /**
     * 类型
     */
    private String type;

    /**
     * 字段名称
     */
    private String name;
    /**
     * 老的值
     */
    private String oldValue;
    /**
     * 新的值
     */
    private String newValue;

    /**
     * 集合新增的值
     */
    private List<String> collectionAddValue;
    /**
     * 集合移除的值
     */
    private List<String> collectionRemoveValue;

    public ObjectCompareResult() {
    }

    public ObjectCompareResult(String name, String oldValue, String newValue) {
        this.type = ObjectCompareResultTypeEnum.STRING.getCode();
        this.name = name;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public ObjectCompareResult(String type, String name, List<String> collectionAddValue, List<String> removeCollectionValue) {
        this.type = type;
        this.name = name;
        this.collectionAddValue = collectionAddValue;
        this.collectionRemoveValue = removeCollectionValue;
    }
}

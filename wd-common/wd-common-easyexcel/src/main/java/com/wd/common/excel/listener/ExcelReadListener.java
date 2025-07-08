package com.wd.common.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.wd.common.core.exception.PromptException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/10/12 10:31
 */
@Slf4j
public class ExcelReadListener<T> extends AnalysisEventListener<T> {
    /**
     * excel表头列名
     */
    private List<String> headTitle;

    /**
     * 数据集合
     */
    private final List<T> dataList = new ArrayList<>();

    /**
     * 读取的excel表头
     */
    private Map<Integer, String> templateHeadMap;

    public ExcelReadListener() {
        super();
    }

    public ExcelReadListener(List<String> headTitle) {
        this.headTitle = headTitle;
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        dataList.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("Excel读取完成,读取数据行数:{}", dataList.size());
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        super.invokeHeadMap(headMap, context);

        this.templateHeadMap = new HashMap<>(headMap);
    }

    /**
     * 获取数据
     */
    public List<T> getDataList() {
        return dataList;
    }


    /**
     * 验证表头是否正确
     */
    public void validateTitle() {
        if (Objects.isNull(headTitle) || headTitle.isEmpty()) {
            throw new PromptException("Excel表头列名不能为空");
        }

        if (templateHeadMap.size() != headTitle.size()) {
            throw new PromptException("模板不正确，请下载最新模板导入");
        }


        for (Map.Entry<Integer, String> entry : templateHeadMap.entrySet()) {
            if (!headTitle.contains(entry.getValue())) {
                throw new PromptException("模板不正确，请下载最新模板导入");
            }
        }
    }
}

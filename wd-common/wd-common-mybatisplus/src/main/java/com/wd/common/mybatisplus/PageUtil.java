package com.wd.common.mybatisplus;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wd.common.core.model.BaseQuery;
import com.wd.common.core.model.PageInfo;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author huangwenda
 * @date 2023/1/31 09:45
 **/
@SuppressWarnings("all")
public class PageUtil {
    /**
     * 升序
     */
    public static final String ASC = "ASC";
    /**
     * 降序
     */
    public static final String DESC = "DESC";


    public static <T> PageInfo<T> buildPageInfo(BaseQuery pageParam) {
        return PageInfo.<T>builder()
                .current(defaultCurrent(pageParam.getCurrent()))
                .size(pageParam.getSize())
                .build();
    }

    public static <T> PageInfo<T> buildPageInfo(BaseQuery pageParam, int total, List<T> records) {
        return PageInfo.<T>builder()
                .current(defaultCurrent(pageParam.getCurrent()))
                .size(pageParam.getSize())
                .total(total)
                .records(records)
                .build();
    }

    public static <T, R> PageInfo<R> buildPageInfo(BaseQuery pageParam, int total, List<T> records, Function<? super T, ? extends R> mapper) {
        return PageInfo.<R>builder()
                .current(defaultCurrent(pageParam.getCurrent()))
                .size(pageParam.getSize())
                .total(total)
                .records(records.stream().map(mapper).collect(Collectors.toList()))
                .build();
    }

    public static <T, R> PageInfo<R> buildPageInfo(Page<T> page, Function<? super T, ? extends R> mapper) {
        return PageInfo.<R>builder()
                .current((int)page.getCurrent())
                .size((int)page.getSize())
                .total((int)page.getTotal())
                .records(page.getRecords().stream().map(mapper).collect(Collectors.toList()))
                .build();
    }

    public static int getTotalPage(PageInfo pageInfo) {
        return getTotalPage(pageInfo.getTotal(), pageInfo.getSize());
    }

    public static int getTotalPage(int total, int size) {
        if (size == 0) {
            return 0;
        } else {
            return total % size == 0 ? total / size : total / size + 1;
        }
    }

    public static int getPageQueryTotal(BaseQuery pageParam) {
        return pageParam.getCurrent() * pageParam.getSize();
    }


    private static int defaultCurrent(int current) {
        return current > 0 ? current : 1;
    }

//    public static <T> Page<T> toPage(BaseQuery baseQuery) {
//        return new Page<>(baseQuery.getCurrent(), baseQuery.getSize());
//    }

    public static <T> Page<T> toPage(BaseQuery baseQuery) {
        Page<T> page = new Page<>(baseQuery.getCurrent(), baseQuery.getSize());

        // 判断是否设置了排序字段
        String sort = baseQuery.getSort();
        String order = baseQuery.getOrder();

        String mappedField = null;
        // 根据 VO 字段映射到数据库字段名
        if (StrUtil.isNotBlank(sort)) {
            mappedField = FIELD_MAPPING.getOrDefault(sort, sort);// 映射不到就用原字段名
        }

        if (sort != null && !sort.isBlank()) {
            if (ASC.equalsIgnoreCase(order)) {
                page.addOrder(OrderItem.asc(mappedField));
            } else {
                page.addOrder(OrderItem.desc(mappedField)); // 默认 DESC
            }
        }
        return page;
    }

    private static final Map<String, String> FIELD_MAPPING = Map.of(
            "startDate", "start_date",
            "endDate", "end_date",
            "createTime","create_time"
    );

}

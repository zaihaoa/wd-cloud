package com.wd.common.mybatisplus;

import com.wd.common.core.BaseQuery;
import com.wd.common.core.PageInfo;

import java.util.List;

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

    public static <T> PageInfo<T> buildPageInfo(BaseQuery pageParam, long total, List<T> records) {
        return PageInfo.<T>builder()
                .current(defaultCurrent(pageParam.getCurrent()))
                .size(pageParam.getSize())
                .total(total)
                .records(records)
                .build();
    }

    public static long getTotalPage(PageInfo pageInfo) {
        return getTotalPage(pageInfo.getTotal(), pageInfo.getSize());
    }

    public static long getTotalPage(long total, long size) {
        if (size == 0L) {
            return 0L;
        } else {
            return total % size == 0L ? total / size : total / size + 1L;
        }
    }


    private static long defaultCurrent(long current) {
        return current > 0 ? current : 1;
    }
}

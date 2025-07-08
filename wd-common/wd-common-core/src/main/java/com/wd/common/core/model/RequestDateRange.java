package com.wd.common.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author huangwenda
 * @date 2022/8/16 14:02
 **/
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDateRange {
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    @Override
    public String toString() {
        return String.format("时间范围[%s]-[%s]", startTime, endTime);
    }
}

package com.wd.common.core.model;

import java.time.LocalDateTime;

/**
 * @author huangwenda
 * @date 2022/9/30 14:00
 **/
@FunctionalInterface
public interface PullDateConsumer {

    void accept(LocalDateTime startTime, LocalDateTime endTime);
}

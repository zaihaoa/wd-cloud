package com.wd.common.server.util;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.wd.common.core.exception.NeedRetryException;
import com.wd.common.core.model.PullDateConsumer;
import com.wd.common.core.model.R;
import com.wd.common.core.model.RequestDateRange;
import com.wd.common.core.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 接口调用重试url
 * call
 *
 * @author huangwenda
 * @date 2022/9/28 19:08
 **/
@Slf4j
public class JobUtil {


    public static LocalDateTime cutDatePeriodPull(LocalDateTime startPullTime, LocalDateTime endPullTime, int intervalDay, PullDateConsumer consumer) {
        return cutDatePeriodPull(startPullTime, endPullTime, intervalDay, 0, consumer);
    }

    /**
     * 切割时间段同步
     *
     * @param startPullTime    开始拉取时间
     * @param endPullTime      结束拉取时间
     * @param intervalDay      切割周期(天)
     * @param consumer         真正执行的业务代码
     * @param everyWaitSeconds 每次执行等待多少秒,如果小于0不睡眠
     * @return 上次同步成功的开始时间
     */
    public static LocalDateTime cutDatePeriodPull(LocalDateTime startPullTime, LocalDateTime endPullTime, int intervalDay, int everyWaitSeconds, PullDateConsumer consumer) {
        // 切割每{intervalDay}天同步一次,防止一次同步时间过长
        List<RequestDateRange> dateRanges = DateUtil.getEveryPeriod(startPullTime, endPullTime, intervalDay);
        int size = dateRanges.size();

        log.info("总时间段为[{}]-[{}],按每{}天切割,一共分割为{}段", LocalDateTimeUtil.formatNormal(startPullTime), LocalDateTimeUtil.formatNormal(endPullTime), intervalDay, size);

        // 上次成功时间
        LocalDateTime lastSuccessTime = startPullTime;

        for (int i = 0; i < size; i++) {
            RequestDateRange dateRange = dateRanges.get(i);
            int part = i + 1;

            LocalDateTime startTime = dateRange.getStartTime();
            LocalDateTime endTime = dateRange.getEndTime();

            log.info("当前同步第{}段,一共{}段,同步时间范围:[{}]-[{}]", part, size, LocalDateTimeUtil.formatNormal(startTime), LocalDateTimeUtil.formatNormal(endTime));

            try {
                // 执行业务逻辑代码
                consumer.accept(startTime, endTime);

                lastSuccessTime = endTime;

                // 线程睡眠
                if (everyWaitSeconds > 0) {
                    TimeUnit.SECONDS.sleep(everyWaitSeconds);
                }
            } catch (Exception e) {
                String errorMsg = String.format("切割时间段同步报错:%s", e.getMessage());
                log.error(errorMsg, e);

                return lastSuccessTime;
            }
        }
        return lastSuccessTime;
    }

    /**
     * 请求平台接口,如果发生网络错误则抛出{@link NeedRetryException}异常时重试
     * 重试{maxAttempts}次
     * 重试间隔时间{backOffPeriod}ms
     */
    @SuppressWarnings("unchecked")
    public static <T> R<T> requestRetry(int maxAttempts, long backOffPeriod, Supplier<R<T>> supplier) {
        // 构建重试模板实例
        RetryTemplate retryTemplate = new RetryTemplate();
        // 设置重试策略,抛出NeedRetryException异常时重试
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(maxAttempts, new BinaryExceptionClassifier(Collections.singletonList(NeedRetryException.class))));

        // 设置重试回退操作策略，主要设置重试间隔时间
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        // 重试间隔时间ms
        backOffPolicy.setBackOffPeriod(backOffPeriod);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        String attributeKey = "attributeKey";

        return retryTemplate.execute(
                retryContext -> {
                    try {
                        R<T> response = supplier.get();
                        retryContext.setAttribute(attributeKey, response);

                        return response;
                    } catch (Exception e) {
                        log.error("请求报错", e);
                        throw new NeedRetryException();
                    }
                },
                retryContext -> {
                    log.warn("已达到最大重试次数:{}", retryContext.getRetryCount());
                    return (R<T>) retryContext.getAttribute(attributeKey);
                }
        );
    }
}

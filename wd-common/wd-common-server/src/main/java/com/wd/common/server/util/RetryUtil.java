package com.wd.common.server.util;


import com.wd.common.core.exception.NeedRetryException;
import com.wd.common.core.model.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Collections;
import java.util.function.Supplier;

/**
 *
 *
 * @author huangwenda
 * @date 2024/3/29 14:41
 **/
@Slf4j
public class RetryUtil {


    /**
     * 重试模板
     * 1、重试{marAttempts}次
     * 2、重试间隔时间{backOffPeriod}ms
     * 3、抛出{@link NeedRetryException}异常时重试
     */
    public static RetryTemplate retryTemplate(int marAttempts, long backOffPeriod) {
        // 构建重试模板实例
        RetryTemplate retryTemplate = new RetryTemplate();
        // 设置重试策略,重试3次,抛出OmsPushException异常时重试
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(marAttempts, new BinaryExceptionClassifier(Collections.singletonList(NeedRetryException.class))));

        // 设置重试回退操作策略，主要设置重试间隔时间
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        // 重试间隔时间ms
        backOffPolicy.setBackOffPeriod(backOffPeriod);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }

    /**
     * 请求平台接口,如果发生网络错误则抛出{@link NeedRetryException}异常时重试
     * 重试{maxAttempts}次
     * 重试间隔时间{backOffPeriod}ms
     */
    @SuppressWarnings("unchecked")
    public static <T> R<T> requestRetryReturnR(int maxAttempts, long backOffPeriod, Supplier<R<T>> supplier) {
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

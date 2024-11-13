package com.wd.common.sentinel.config;

import com.alibaba.csp.sentinel.adapter.dubbo3.config.DubboAdapterGlobalConfig;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.wd.common.core.ResponseCodeEnum;
import com.wd.common.core.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author huangwenda
 * @date 2023/8/29 13:40
 **/
@Slf4j
@Component
public class DubboFallbackRegister implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        DubboAdapterGlobalConfig.setProviderFallback((invoker, invocation, ex) -> {
            if (ex instanceof FlowException) {
                // 除了qps限流,其他规则待处理
                FlowRule rule = (FlowRule) ex.getRule();
                double ruleCount = rule.getCount();
                log.warn("触发限流,resource:{},qps:{}", rule.getResource(), ruleCount);
                return AsyncRpcResult.newDefaultAsyncResult(RpcResponse.failure(ResponseCodeEnum.TOO_MANY_REQUESTS.getCode(), "请求过于频繁,QPS不能超过" + ruleCount), invocation);
            }
            // 默认处理
            return AsyncRpcResult.newDefaultAsyncResult(ex.toRuntimeException(), invocation);
        });
    }
}

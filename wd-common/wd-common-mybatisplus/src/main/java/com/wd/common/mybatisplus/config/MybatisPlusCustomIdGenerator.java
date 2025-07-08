package com.wd.common.mybatisplus.config;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.stereotype.Component;

/**
 * @author huangwenda
 * @date 2023/8/22 10:03
 **/
@Component
public class MybatisPlusCustomIdGenerator implements IdentifierGenerator {
    @Override
    public Long nextId(Object entity) {
        return IdWorker.getId();
    }
}

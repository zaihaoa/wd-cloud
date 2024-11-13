package com.wd.common.redis;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author huangwenda
 * @date 2023/2/20 17:59
 **/
@ToString
@EqualsAndHashCode
public class CacheEmpty implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public static final CacheEmpty EMPTY = new CacheEmpty();
}

package com.wd.common.redis;

import com.wd.common.core.model.CodeDescEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author huangwenda
 * @date 2024/1/7 10:11
 **/
@Getter
@AllArgsConstructor
public enum CacheRegion implements CodeDescEnum {
    MYBATIS_PLUS("mybatis_plus", "MybatisPlus"),
    CODE_GENERATOR("code_generator", "编号生成器"),
    SYSTEM("system", "系统"),
    MENU("menu", "菜单"),
    USER("user", "用户"),
    ;

    private final String code;
    private final String desc;
}

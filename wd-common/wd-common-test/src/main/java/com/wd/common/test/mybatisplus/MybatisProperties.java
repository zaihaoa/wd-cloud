package com.wd.common.test.mybatisplus;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author huangwenda
 * @date 2022/11/26 10:48
 **/
@Getter
@Setter
@ToString
public class MybatisProperties {
    private String jdbcUrl;

    private String driver;

    private String username;

    private String password;

    private String mapperPackage;

    private String xmlPackage;
}

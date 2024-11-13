package com.wd;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author huangwenda
 * @date 2023/2/13 17:28
 **/
@SpringBootApplication
@EnableDubbo
public class FileCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileCenterApplication.class, args);
    }
}

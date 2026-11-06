package com.wd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author huangwenda
 * @date 2023/2/13 17:28
 **/
@SpringBootApplication
@EnableFeignClients(basePackages = "com.wd")
public class SystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }
}

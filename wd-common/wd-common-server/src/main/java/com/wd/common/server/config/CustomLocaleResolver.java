package com.wd.common.server.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

/**
 * @author huangwenda
 * @date 2024/1/16 20:55
 **/
public class CustomLocaleResolver implements LocaleResolver {
    //解析请求
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String language = request.getHeader("lang");
        Locale locale = Locale.CHINA; // 如果没有获取到就使用系统默认的
        //如果请求链接不为空
        if (StringUtils.isNotBlank(language)) {
            //分割请求参数
            String[] split = language.split("_");
            //国家，地区
            locale = new Locale(split[0], split[1]);
        }
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {
    }
}

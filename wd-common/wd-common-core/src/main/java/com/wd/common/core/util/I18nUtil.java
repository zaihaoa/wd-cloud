package com.wd.common.core.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author huangwenda
 * @date 2024/1/16 21:06
 **/
@Component
public class I18nUtil {

    // 如果当前bean不加@Component注解，则messageSource无法注入，始终为null
    private static MessageSource messageSource;

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        I18nUtil.messageSource = messageSource;
    }

    public static String message(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    /**
     * 解析code对应的信息进行返回，如果对应的code不能被解析则抛出异常NoSuchMessageException
     *
     * @param code 需要进行解析的code，对应资源文件中的一个属性名
     * @param args 当对应code对应的信息不存在时需要返回的默认值
     * @return 国际化翻译值
     */
    public static String message(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    /**
     * 解析code对应的信息进行返回，如果对应的code不能被解析则返回默认信息defaultMessage。
     *
     * @param code           需要进行解析的code，对应资源文件中的一个属性名
     * @param defaultMessage 当对应code对应的信息不存在时需要返回的默认值
     * @param args           需要用来替换code对应的信息中包含参数的内容，如：{0},{1,date},{2,time}
     * @return 国际化翻译值
     */
    public static String message(String code, String defaultMessage, Object... args) {
        return messageSource.getMessage(code, args, defaultMessage, LocaleContextHolder.getLocale());
    }
}

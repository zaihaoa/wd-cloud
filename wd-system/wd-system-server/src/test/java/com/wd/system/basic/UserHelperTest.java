package com.wd.system.basic;

import com.wd.system.basic.util.UserHelper;
import org.junit.jupiter.api.Test;

/**
 *
 *
 * @author huangwenda
 * @date 2026/11/5 16:50
 **/
public class UserHelperTest {

    @Test
    public void countryParseTest() {
        System.out.println("加密:" + UserHelper.encryptPassword("123456"));

    }
}

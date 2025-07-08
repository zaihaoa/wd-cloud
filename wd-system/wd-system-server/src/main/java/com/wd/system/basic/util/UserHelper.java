package com.wd.system.basic.util;

import cn.hutool.crypto.digest.BCrypt;

/**
 * @author huangwenda
 * @date 2024/1/9 17:09
 **/
public class UserHelper {

    /**
     * 加密密码
     *
     * @param password 未加密的密码
     * @return 加密后的密码
     */
    public static String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(10));
    }

    /**
     * 校验密码
     *
     * @param password        未加密的密码
     * @param encryptPassword 加密后的密码
     * @return 是否是同一个密码
     */
    public static boolean checkPassword(String password, String encryptPassword) {
        return BCrypt.checkpw(password, encryptPassword);
    }
}

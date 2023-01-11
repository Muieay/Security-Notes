package org.mui.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BaseUtil {

    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }


    /**
     * 验证密码
     * @param pwd   原密码
     * @param encodePwd  加密密码字符串
     * @return
     */
    public static Boolean decryptPassword(String pwd,String encodePwd) {
        return new BCryptPasswordEncoder().matches(pwd,encodePwd);
    }
}

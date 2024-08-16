package com.example.videoweb.utils;

import cn.hutool.crypto.digest.BCrypt;

/**
 * @Author: chailei
 * @Date: 2024/8/15 17:39
 */
public class PassWordUtil {

    /**
     * 密码加密
     */
    public static String encrypt(String source){
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(source, salt);
    }

    /**
     * 密码校验
     */
    public static boolean check(String source, String pwdCode){
        return BCrypt.checkpw(source, pwdCode);
    }

    public static void main(String[] args) {
        String str = "123456";
        String encrypt = encrypt(str);
        System.out.println(encrypt);
        System.out.println(check(str, encrypt));
    }
}


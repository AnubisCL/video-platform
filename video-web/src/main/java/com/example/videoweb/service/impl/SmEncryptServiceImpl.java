package com.example.videoweb.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.symmetric.SM4;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.example.videoweb.service.IEncryptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author: chailei
 * @Date: 2024/8/16 09:53
 */
@Slf4j
@Service("smEncryptService")
public class SmEncryptServiceImpl implements IEncryptService {

    /**
     * SM4加密秘钥，长度必须为16字节，即128位
     */
    public static final String SM4_KEY = "1q2w3e4R5T6Y7U!@";

    //指明加密算法和秘钥
    private static SymmetricCrypto sm4 = new SM4(SM4_KEY.getBytes());

    /**
     * 数据加密
     * @param data
     * @return
     */
    @Override
    public String encrypt(String data) {
        if (StrUtil.isBlank(data)) {
            return "";
        }
        try {
            String result = sm4.encryptHex(data);
            return result;
        } catch (Exception e) {
            log.error("数据加密失败：{}", e.getMessage(), e);
            return "";
        }
    }

    /**
     * 数据解密
     * @param data
     * @return
     */
    @Override
    public String decrypt(String data) {
        if (StrUtil.isBlank(data)) {
            return "";
        }
        try {
            String result = sm4.decryptStr(data);
            return result;
        } catch (Exception e) {
            log.error("数据解密失败：{}", e.getMessage(), e);
            return "";
        }
    }

    public static void main(String[] args) {
        String data = "{\"username\": \"\",\"passwordHash\": \"123456\",\"email\": \"anubis@163.com\",\"signType\": \"signIn\"}";
        String result = sm4.encryptHex(data);
        System.out.println(result);
    }

}

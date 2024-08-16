package com.example.videoweb.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.example.videoweb.service.IEncryptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @Author: chailei
 * @Date: 2024/8/16 09:53
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "dreamcrafter.encrypt.type", havingValue = "sm4")
public class SmEncryptServiceImpl implements IEncryptService {

    /**
     * SM4加密秘钥，长度必须为16字节，即128位
     */
    public static final String SM4_KEY = "DreamCrafter5219";

    //指明加密算法和秘钥
    private static SymmetricCrypto sm4 = new SymmetricCrypto("SM4/ECB/PKCS5Padding", SM4_KEY.getBytes());

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
        String data = "{\"bucketName\":\"test-bucket\",\"fileName\":\"string\"}";
        String result = sm4.encryptHex(data);
        System.out.println(result);
    }

}

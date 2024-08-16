package com.example.videoweb.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.AsymmetricCrypto;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.example.videoweb.base.config.CacheConfig;
import com.example.videoweb.domain.cache.RSAInfo;
import com.example.videoweb.service.IEncryptService;
import com.example.videoweb.utils.RSAUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


/**
 * @Author: chailei
 * @Date: 2024/8/16 10:54
 */
@Slf4j
@Service("rsaEncryptService")
public class RsaEncryptServiceImpl implements IEncryptService {

    private static AsymmetricCrypto rsa = new RSA();
    @Resource @Qualifier("ehCacheManager") private CacheManager ehCacheManager;
    private static Cache<Long, RSAInfo> rsaInfoCache;

    @PostConstruct
    public void init() {
        rsaInfoCache = ehCacheManager.getCache(CacheConfig.RSA_CACHE_NAME, Long.class, RSAInfo.class);
    }

    /**
     * 加密
     * @param data
     * @return
     */
    @Override
    public String encrypt(String data) {
        if (StrUtil.isBlank(data)) {
            return "";
        }
        Long userId = StpUtil.getLoginIdAsLong();
        String publicKey = rsaInfoCache.get(userId).getPublicKey();
        // 使用公钥加密
        byte[] bytes = new byte[0];
        try {
            bytes = RSAUtil.encryptByPublicKey(data.getBytes(), publicKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new String(bytes);
    }

    /**
     * 解密
     * @param data
     * @return
     */
    @Override
    public String decrypt(String data) {
        if (StrUtil.isBlank(data)) {
            return "";
        }
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            String privateKey = rsaInfoCache.get(userId).getPrivateKey();
            // 使用私钥解密
            byte[] rs = Base64.decodeBase64(data);
            String resData = new String(RSAUtil.decryptByPrivateKey(rs, privateKey),"UTF-8");
            byte[] decryptedBytes = rsa.decrypt(resData, KeyType.PrivateKey);
            return new String(decryptedBytes);
        } catch (Exception e) {
            log.error("Decryption failed", e);
            return null;
        }
    }

}

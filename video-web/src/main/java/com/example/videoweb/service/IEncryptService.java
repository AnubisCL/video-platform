package com.example.videoweb.service;

/**
 * @Author: chailei
 * @Date: 2024/8/16 09:53
 */
public interface IEncryptService {

    /**
     * 加密
     *
     * @param data
     * @return
     */
    String encrypt(String data);

    /**
     * 解密
     * @param data
     * @return
     */
    String decrypt(String data);

}

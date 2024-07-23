package com.example.videoweb.domain.enums;

/**
 * @Author: chailei
 * @Date: 2024/7/23 21:04
 */
public enum ErrorCode {

    // 预定的状态码
    SUCCESS(0,"ok"),
    ERROR(1,"error");

    private int code;
    private String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

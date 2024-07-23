package com.example.videoweb.domain.enums;

/**
 * @Author: chailei
 * @Date: 2024/7/23 17:54
 */
public enum StatusEnum {

    YES(0),
    NO(1);

    private Integer status;

    StatusEnum(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}

package com.example.videoweb.domain.enums;

/**
 * @Author: chailei
 * @Date: 2024/7/23 17:54
 */
public enum RoleEnum {

    ADMIN(1L),
    NORMAL_CUSTOMER(2L),
    VIP_CUSTOMER(3L),
    BLACK_CUSTOMER(4L);

    private Long code;

    RoleEnum(Long code) {
        this.code = code;
    }

    public Long getCode() {
        return code;
    }
}

package com.example.videoweb.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @Author: chailei
 * @Date: 2024/10/16 12:29
 */
public enum OrderState {
    WAITING_CONFIRM(0, "WAITING_CONFIRM"), //下单待确认-购物车
    WAITING_COMPLETED(1, "WAITING_COMPLETED"), //下单待完成
    COMPLETED(2, "COMPLETED"), //下单完成
    FAILED(3, "FAILED"); //下单取消

    @EnumValue
    private final Integer code;

    private final String desc;
    OrderState(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getCode() {
        return code;
    }
}

package com.example.videoweb.domain.enums;

/**
 * @Author: chailei
 * @Date: 2024/11/26 20:41
 */
public enum MsgEnum {

    //default、primary、success、warning、danger
    DEFAULT("default"),
    PRIMARY("primary"),
    SUCCESS("success"),
    WARNING("warning"),
    DANGER("danger");

    private String type;

    MsgEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

package com.example.videoweb.domain.enums;

/**
 * @Author: chailei
 * @Date: 2024/11/26 20:51
 */
public enum WSEnum {
    HEART("HEART"),
    SHOW_NOTIFY("SHOW_NOTIFY"),
    SHOW_TOAST("SHOW_TOAST"),
    GAME_MESSAGE("GAME_MESSAGE");

    private String type;

    WSEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

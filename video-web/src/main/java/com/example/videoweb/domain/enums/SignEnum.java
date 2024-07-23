package com.example.videoweb.domain.enums;

/**
 * @Author: chailei
 * @Date: 2024/7/23 17:54
 */
public enum SignEnum {

    SIGN_IN("signIn"),
    LONG_IN("loginIn"),
    SIGN_OUT("signOut");

    private String type;

    SignEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static SignEnum getEnum(String type) {
        return switch (type) {
            case "signIn" -> SIGN_IN;
            case "loginIn" -> LONG_IN;
            case "signOut" -> SIGN_OUT;
            default -> null;
        };
    }
}

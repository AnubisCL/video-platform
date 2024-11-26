package com.example.videoweb.domain;

import lombok.*;

/**
 * @Author: chailei
 * @Date: 2024/11/4 22:24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WSMessage {

    private String msg;
    private String msgType;    //default、primary、success、warning、danger
    private String type;    //showNotify，showToast，gameMessage
    private String code;
}

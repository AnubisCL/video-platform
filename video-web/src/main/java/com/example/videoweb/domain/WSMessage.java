package com.example.videoweb.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: chailei
 * @Date: 2024/11/4 22:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WSMessage {

    private String type;
    private String msg;
}

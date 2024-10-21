package com.example.videoweb.domain.dto;

import lombok.Data;

/**
 * @Author: chailei
 * @Date: 2024/10/22 00:43
 */
@Data
public class ConfirmOrderDto {

    private Long orderId;
    private String orderType;
    private String orderDate;
    private String orderRemark;
}

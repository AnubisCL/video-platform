package com.example.videoweb.domain.vo;

import com.example.videoweb.base.annotation.ReplaceIpEntity;
import com.example.videoweb.domain.entity.OrderItem;
import com.example.videoweb.domain.entity.Product;
import lombok.Data;

/**
 * @Author: chailei
 * @Date: 2024/10/21 17:34
 */

@Data
@ReplaceIpEntity
public class OrderItemInfoVo {
    private OrderItem orderItem;
    private String description;
    private String categoryName;
    @ReplaceIpEntity
    private Product product;
}

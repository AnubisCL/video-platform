package com.example.videoweb.domain.vo;

import com.example.videoweb.base.annotation.ReplaceIpEntity;
import com.example.videoweb.domain.entity.Product;
import lombok.Data;

import java.util.List;

/**
 * @Author: chailei
 * @Date: 2024/10/21 22:40
 */
@Data
@ReplaceIpEntity
public class CartHisInfoVo {
    private Long currentOrderId;
    @ReplaceIpEntity
    private List<Product> cartItemList;
}

package com.example.videoweb.domain.vo;

import com.example.videoweb.base.annotation.ReplaceIpEntity;
import com.example.videoweb.domain.entity.Order;
import lombok.Data;

import java.util.List;

/**
 * @Author: chailei
 * @Date: 2024/10/21 17:32
 */
@Data
@ReplaceIpEntity
public class OrderDetailInfoVo {

    private Order orderInfo;
    @ReplaceIpEntity
    private List<OrderItemInfoVo> orderItemList;
}

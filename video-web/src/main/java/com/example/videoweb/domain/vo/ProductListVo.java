package com.example.videoweb.domain.vo;

import com.example.videoweb.base.annotation.ReplaceIpEntity;
import com.example.videoweb.domain.entity.Product;
import lombok.Data;

import java.util.List;

/**
 * @Author: chailei
 * @Date: 2024/10/21 16:55
 */
@Data
@ReplaceIpEntity
public class ProductListVo {

    private String index;
    @ReplaceIpEntity
    private List<Product> cardList;
}

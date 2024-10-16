package com.example.videoweb.service.impl;

import com.example.videoweb.domain.entity.ProductDetail;
import com.example.videoweb.mapper.ProductDetailMapper;
import com.example.videoweb.service.IProductDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品详情表 服务实现类
 * </p>
 *
 * @author anubis
 * @since 2024-10-16
 */
@Service
public class ProductDetailServiceImpl extends ServiceImpl<ProductDetailMapper, ProductDetail> implements IProductDetailService {

}

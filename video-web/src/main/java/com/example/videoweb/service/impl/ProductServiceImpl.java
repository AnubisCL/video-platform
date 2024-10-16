package com.example.videoweb.service.impl;

import com.example.videoweb.domain.entity.Product;
import com.example.videoweb.mapper.ProductMapper;
import com.example.videoweb.service.IProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author anubis
 * @since 2024-10-16
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

}

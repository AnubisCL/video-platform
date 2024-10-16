package com.example.videoweb.service.impl;

import com.example.videoweb.domain.entity.ProductCategory;
import com.example.videoweb.mapper.ProductCategoryMapper;
import com.example.videoweb.service.IProductCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品类别表 服务实现类
 * </p>
 *
 * @author anubis
 * @since 2024-10-16
 */
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements IProductCategoryService {

}

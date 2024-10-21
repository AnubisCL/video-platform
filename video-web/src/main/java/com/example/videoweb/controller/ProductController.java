package com.example.videoweb.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.videoweb.base.annotation.ReplaceIpFun;
import com.example.videoweb.domain.entity.Product;
import com.example.videoweb.domain.entity.ProductCategory;
import com.example.videoweb.domain.entity.ProductDetail;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.domain.vo.ProductListVo;
import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.service.IProductCategoryService;
import com.example.videoweb.service.IProductDetailService;
import com.example.videoweb.service.IProductService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author anubis
 * @since 2024-10-16
 */
@SaCheckLogin
@RestController
@RequestMapping("/product/")
public class ProductController {

    @Resource private IProductService productService;
    @Resource private IProductCategoryService productCategoryService;
    @Resource private IProductDetailService productDetailService;

    @GetMapping("getProductCategory")
    public ResultVo getProductCategory() {
        return ResultVo.data(productCategoryService.lambdaQuery()
                .select(ProductCategory::getCategoryId, ProductCategory::getCategoryName)
                .eq(ProductCategory::getStatus, StatusEnum.YES.getStatus())
                .orderByAsc(ProductCategory::getCategoryId)
                .list());
    }

    @PostMapping("updateProductInfo")
    public ResultVo updateProductInfo(@RequestBody Product product) {
        product.setUpdateDate(new Date());
        product.setCreateDate(new Date());
        return ResultVo.data(productService.updateById(product));
    }

    @ReplaceIpFun
    @GetMapping("getProductList")
    public ResultVo getProductList() {
        List<Product> productList = productService.lambdaQuery()
                .eq(Product::getStatus, StatusEnum.YES.getStatus()).list();
        Map<Long, List<Product>> collect = productList.stream()
                .collect(Collectors.groupingBy(Product::getCategoryId));
        Set<Long> categoryIds = collect.keySet();
        List<ProductListVo> mapList = categoryIds.parallelStream().map(id -> {
            ProductListVo productListVo = new ProductListVo();
            productListVo.setIndex(productCategoryService.getById(id).getCategoryName());
            productListVo.setCardList(collect.get(id));
            return productListVo;
        }).collect(Collectors.toList());
        return ResultVo.data(mapList);
    }

    @GetMapping("getProductDetail/{productDetailId}")
    public ResultVo getProductDetail(@PathVariable("productDetailId")Long productDetailId) {
        ProductDetail detail = productDetailService.getById(productDetailId);
        return ResultVo.data(detail);
    }


}

package com.example.videoweb.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.videoweb.domain.entity.Product;
import com.example.videoweb.domain.entity.ProductCategory;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.service.IProductCategoryService;
import com.example.videoweb.service.IProductService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    @GetMapping("getProductCategory")
    public ResultVo getProductCategory() {
        return ResultVo.data(productCategoryService.lambdaQuery()
                .select(ProductCategory::getCategoryId, ProductCategory::getCategoryName)
                .eq(ProductCategory::getStatus, StatusEnum.YES.getStatus())
                .orderByAsc(ProductCategory::getCategoryId)
                .list());
    }

    @GetMapping("getProductList")
    public ResultVo getProductList() {
        List<Product> productList = productService.lambdaQuery()
                .eq(Product::getStatus, StatusEnum.YES.getStatus()).list();
        Map<Long, List<Product>> collect = productList.stream()
                .collect(Collectors.groupingBy(Product::getCategoryId));
        Set<Long> categoryIds = collect.keySet();
        List<HashMap<String, Object>> mapList = categoryIds.parallelStream().map(id -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("index", productCategoryService.getById(id).getCategoryName());
            map.put("cardList", collect.get(id));
            return map;
        }).collect(Collectors.toList());
        return ResultVo.data(mapList);
    }


}

package com.example.videoweb.service.impl;

import com.example.videoweb.domain.entity.Cart;
import com.example.videoweb.mapper.CartMapper;
import com.example.videoweb.service.ICartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 购物车表 服务实现类
 * </p>
 *
 * @author anubis
 * @since 2024-10-16
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements ICartService {

}

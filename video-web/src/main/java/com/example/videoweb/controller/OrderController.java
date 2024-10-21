package com.example.videoweb.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.videoweb.base.annotation.ReplaceIpFun;
import com.example.videoweb.domain.dto.ConfirmOrderDto;
import com.example.videoweb.domain.entity.Order;
import com.example.videoweb.domain.entity.OrderItem;
import com.example.videoweb.domain.entity.Product;
import com.example.videoweb.domain.enums.OrderState;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.domain.vo.CartHisInfoVo;
import com.example.videoweb.domain.vo.OrderDetailInfoVo;
import com.example.videoweb.domain.vo.OrderItemInfoVo;
import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.service.*;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 下单表 前端控制器
 * </p>
 *
 * @author anubis
 * @since 2024-10-16
 */
@SaCheckLogin
@RestController
@RequestMapping("/order/")
public class OrderController {

    @Resource private IOrderService orderService;
    @Resource private IProductService productService;
    @Resource private IProductDetailService productDetailService;
    @Resource private IProductCategoryService productCategoryService;
    @Resource private IOrderItemService orderItemService;

    /**
     * 获取上次选择的购物车信息
     * @return
     */
    @ReplaceIpFun
    @GetMapping("getCartHisInfo/{userId}")
    public ResultVo getCartHisInfo(@PathVariable("userId")Long userId) {
        List<Order> orders = orderService.lambdaQuery()
                .eq(Order::getUserId, userId)
                .eq(Order::getStatus, StatusEnum.YES)
                .eq(Order::getOrderStatus, OrderState.WAITING_CONFIRM)
                .orderByDesc(Order::getUpdateDate)
                .list();
        Order order = orders.isEmpty() ? orderService.create(userId) : orders.get(0);
        List<OrderItem> orderItems = orderItemService.lambdaQuery()
                .eq(OrderItem::getOrderId, order.getOrderId())
                .eq(OrderItem::getUserId, userId)
                .eq(OrderItem::getStatus, StatusEnum.YES)
                .gt(OrderItem::getQuantity, 0L)
                .list();
        List<Product> cartList = orderItems.parallelStream().map(item -> {
            Product product = productService.getById(item.getProductId());
            product.setStock(item.getQuantity());
            return product;
        }).collect(Collectors.toList());
        CartHisInfoVo cartHisInfoVo = new CartHisInfoVo();
        cartHisInfoVo.setCurrentOrderId(order.getOrderId());
        cartHisInfoVo.setCartItemList(cartList);
        return ResultVo.data(cartHisInfoVo);
    }

    @GetMapping("clearCartHisInfo/{userId}")
    public ResultVo clearCartHisInfo(@PathVariable("userId")Long userId) {
        List<Order> orders = orderService.lambdaQuery()
                .eq(Order::getUserId, userId)
                .eq(Order::getStatus, StatusEnum.YES)
                .eq(Order::getOrderStatus, OrderState.WAITING_CONFIRM)
                .orderByAsc(Order::getUpdateDate)
                .list();
        if (!orders.isEmpty()) {
            orders.parallelStream().forEach(order -> {
                orderService.removeById(order.getOrderId());
                List<OrderItem> orderItems = orderItemService.lambdaQuery()
                        .eq(OrderItem::getOrderId, order.getOrderId())
                        .eq(OrderItem::getUserId, userId)
                        .eq(OrderItem::getStatus, StatusEnum.YES).list();
                orderItemService.removeByIds(orderItems.stream().map(OrderItem::getOrderItemId).collect(Collectors.toList()));
            });
        }
        return ResultVo.ok();
    }

    @GetMapping("orderItem/{type}/{userId}/{orderId}/{productId}")
    public ResultVo orderItem(
            @PathVariable(value = "type")String type,
            @PathVariable(value = "userId")Long userId,
            @PathVariable(value = "orderId")Long orderId,
            @PathVariable(value = "productId")Long productId
    ) {
        OrderItem orderItem = new OrderItem();
        orderItem.setUserId(userId);
        orderItem.setOrderId(orderId);
        orderItem.setProductId(productId);
        List<OrderItem> orderItems = orderItemService.lambdaQuery(orderItem).list();
        if (orderItems.isEmpty()) {
            if (type.equals("add")) {
                orderItem.setQuantity(1L);
                orderItemService.save(orderItem);
            } else {
                return ResultVo.error("没有记录需要删除");
            }
        } else {
            Long quantity = orderItems.get(0).getQuantity();
            orderItem.setQuantity(type.equals("add") ? quantity+1L : quantity.equals(0L) ? quantity : quantity-1L);
            orderItem.setOrderItemId(orderItems.get(0).getOrderItemId());
            orderItem.setUpdateDate(new Date());
            orderItemService.updateById(orderItem);
        }
        return ResultVo.ok();
    }

    @PostMapping("confirm")
    public ResultVo confirm(@RequestBody ConfirmOrderDto confirmOrderDto) {
        Order confirm = orderService.confirm(confirmOrderDto);
        return ResultVo.data(confirm);
    }

    @ReplaceIpFun
    @GetMapping("getOrderDetailInfo/{userId}")
    public ResultVo getOrderDetailInfo(@PathVariable(value = "userId")Long userId) {
        List<Order> orders = orderService.getOrders(userId);
        List<OrderDetailInfoVo> orderDetailInfoVos = orders.stream().map(order -> {
            OrderDetailInfoVo orderDetailInfoVo = new OrderDetailInfoVo();
            orderDetailInfoVo.setOrderInfo(order);
            List<OrderItem> orderItems = orderItemService.lambdaQuery()
                    .eq(OrderItem::getStatus, StatusEnum.YES)
                    .eq(OrderItem::getOrderId, order.getOrderId()).list();
            List<OrderItemInfoVo> orderItemInfoVos = orderItems.parallelStream().map(item -> {
                Product product = productService.getById(item.getProductId());
                String description = productDetailService.getById(product.getProductDetailId()).getDescription();
                String categoryName = productCategoryService.getById(product.getCategoryId()).getCategoryName();
                OrderItemInfoVo orderItemInfoVo = new OrderItemInfoVo();
                orderItemInfoVo.setOrderItem(item);
                orderItemInfoVo.setCategoryName(categoryName);
                orderItemInfoVo.setDescription(description);
                orderItemInfoVo.setProduct(product);
                return orderItemInfoVo;
            }).collect(Collectors.toList());
            orderDetailInfoVo.setOrderItemList(orderItemInfoVos);
            return orderDetailInfoVo;
        }).collect(Collectors.toList());
        return ResultVo.data(orderDetailInfoVos);
    }

    @GetMapping("getOrderSize/{userId}")
    public ResultVo getOrderSize(@PathVariable(value = "userId")Long userId) {
        List<Order> orders = orderService.
                lambdaQuery().eq(Order::getStatus, StatusEnum.YES.getStatus())
                .in(Order::getOrderStatus, OrderState.WAITING_COMPLETED)
                //.eq(Order::getUserId, userId)
                .list();
        return ResultVo.data(orders.size());
    }

    @GetMapping("done/{orderId}")
    public ResultVo done(@PathVariable(value = "orderId")Long orderId) {
        return ResultVo.data(orderService.done(orderId));
    }
    @GetMapping("back/{orderId}")
    public ResultVo back(@PathVariable(value = "orderId")Long orderId) {
        return ResultVo.data(orderService.back(orderId));
    }
    @GetMapping("cancel/{orderId}")
    public ResultVo cancel(@PathVariable(value = "orderId")Long orderId) {
        return ResultVo.data(orderService.cancel(orderId));
    }


}

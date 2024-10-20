package com.example.videoweb.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.videoweb.domain.entity.Order;
import com.example.videoweb.domain.entity.OrderItem;
import com.example.videoweb.domain.entity.Product;
import com.example.videoweb.domain.enums.OrderState;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.service.*;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
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
        HashMap<String, Object> map = new HashMap<>();
        map.put("currentOrderId", order.getOrderId());
        map.put("cartItemList", cartList);
        return ResultVo.data(map);
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

    @GetMapping("confirm/{orderId}")
    public ResultVo confirm(@PathVariable(value = "orderId")Long orderId) {
        Order confirm = orderService.confirm(orderId);
        return ResultVo.data(confirm);
    }

    @GetMapping("getOrderDetailInfo/{userId}")
    public ResultVo getOrderDetailInfo(@PathVariable(value = "userId")Long userId) {
        List<Order> orders = orderService.getOrders(userId);
        List<HashMap<String, Object>> list = orders.stream().map(order -> {
            HashMap<String, Object> resultMap = new HashMap<>();
            resultMap.put("orderInfo", order);
            List<OrderItem> orderItems = orderItemService.lambdaQuery()
                    .eq(OrderItem::getStatus, StatusEnum.YES)
                    .eq(OrderItem::getOrderId, order.getOrderId()).list();
            List<HashMap<String, Object>> mapList = orderItems.parallelStream().map(item -> {
                Product product = productService.getById(item.getProductId());
                String description = productDetailService.getById(product.getProductDetailId()).getDescription();
                String categoryName = productCategoryService.getById(product.getCategoryId()).getCategoryName();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("orderItem", item);
                hashMap.put("description", description);
                hashMap.put("categoryName", categoryName);
                hashMap.put("product", product);
                return hashMap;
            }).collect(Collectors.toList());
            resultMap.put("orderItemList", mapList);
            return resultMap;
        }).collect(Collectors.toList());
        return ResultVo.data(list);
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

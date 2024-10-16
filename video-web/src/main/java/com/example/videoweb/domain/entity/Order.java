package com.example.videoweb.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import com.example.videoweb.domain.enums.OrderState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 下单表
 * </p>
 *
 * @author anubis
 * @since 2024-10-16
 */
@Getter
@Setter
@TableName("v_order")
@Schema(name = "Order", description = "下单表")
public class Order {

    @Schema(description = "订单ID")
    @TableId(value = "order_id", type = IdType.AUTO)
    private Long orderId;

    @Schema(description = "用户ID关联用户表")
    private Long userId;

    @Schema(description = "订单总价")
    private Long totalPrice;

    //mp指定状态机枚举映射
    @Schema(description = "订单状态（状态机）")
    private OrderState orderStatus;

    @Schema(description = "0:正常,1:锁定")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}

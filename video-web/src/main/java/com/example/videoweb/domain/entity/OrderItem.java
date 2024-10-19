package com.example.videoweb.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 订单条目表
 * </p>
 *
 * @author anubis
 * @since 2024-10-19
 */
@Getter
@Setter
@TableName("v_order_item")
@Schema(name = "OrderItem", description = "订单条目表")
public class OrderItem {

    @Schema(description = "购物车ID")
    @TableId(value = "order_item_id", type = IdType.AUTO)
    private Long orderItemId;

    @Schema(description = "用户ID，关联用户表")
    private Long userId;

    @Schema(description = "商品ID，关联商品表")
    private Long productId;

    @Schema(description = "订单ID，关联订单表")
    private Long orderId;

    @Schema(description = "数量")
    private Long quantity;

    @Schema(description = "0:正常,1:锁定")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}

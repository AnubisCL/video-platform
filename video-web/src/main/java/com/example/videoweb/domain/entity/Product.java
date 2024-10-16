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
 * 商品表
 * </p>
 *
 * @author anubis
 * @since 2024-10-16
 */
@Getter
@Setter
@TableName("v_product")
@Schema(name = "Product", description = "商品表")
public class Product {

    @Schema(description = "商品ID")
    @TableId(value = "product_id", type = IdType.AUTO)
    private Long productId;

    @Schema(description = "关联商品类别表")
    private Long categoryId;

    @Schema(description = "商品详情表")
    private Long productDetailId;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "商品价格")
    private Long price;

    @Schema(description = "库存数量")
    private Long stock;

    @Schema(description = "0:正常,1:锁定")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}

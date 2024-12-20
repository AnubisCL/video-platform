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
 * 商品详情表
 * </p>
 *
 * @author anubis
 * @since 2024-10-16
 */
@Getter
@Setter
@TableName("v_product_detail")
@Schema(name = "ProductDetail", description = "商品详情表")
public class ProductDetail {

    @Schema(description = "商品详情ID")
    @TableId(value = "product_detail_id", type = IdType.AUTO)
    private Long productDetailId;

    @Schema(description = "商品描述（MarkDown）")
    private String description;

    @Schema(description = "0:正常,1:锁定")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}

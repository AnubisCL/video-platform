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
 * 菜单路由表
 * </p>
 *
 * @author anubis
 * @since 2024-07-24
 */
@Getter
@Setter
@TableName("v_menu")
@Schema(name = "Menu", description = "菜单路由表")
public class Menu {

    @Schema(description = "菜单Id")
    @TableId(value = "menu_id", type = IdType.AUTO)
    private Long menuId;

    @Schema(description = "权限Id")
    private Long permissionId;

    @Schema(description = "菜单path")
    private String menuPath;

    @Schema(description = "菜单Icon")
    private String menuIcon;

    @Schema(description = "菜单标题（i18）")
    private String menuTitle;

    @Schema(description = "菜单类型")
    private String menuType;

    @Schema(description = "0:正常,1:失效")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
}

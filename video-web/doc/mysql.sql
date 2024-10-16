use video_db;

CREATE TABLE v_product_category (
    category_id BIGINT AUTO_INCREMENT COMMENT '类别ID',
    category_name VARCHAR(255) NOT NULL COMMENT '类别名称',
    description TEXT COMMENT '类别描述',
    status        smallint default 0 not null comment '0:正常,1:锁定',
    create_date   datetime           not null comment '创建时间',
    update_date   datetime           not null comment '更新时间',
    PRIMARY KEY (category_id)
) COMMENT='商品类别表';

CREATE TABLE v_product (
    product_id BIGINT AUTO_INCREMENT COMMENT '商品ID',
    category_id BIGINT NOT NULL COMMENT '关联商品类别表',
    product_detail_id BIGINT NOT NULL COMMENT '商品详情表',
    name VARCHAR(255) NOT NULL COMMENT '商品名称',
    price BIGINT NOT NULL COMMENT '商品价格',
    stock BIGINT NOT NULL COMMENT '库存数量',
    status        smallint default 0 not null comment '0:正常,1:锁定',
    create_date   datetime           not null comment '创建时间',
    update_date   datetime           not null comment '更新时间',
    PRIMARY KEY (product_id)
) COMMENT='商品表';


CREATE TABLE v_product_detail (
    product_detail_id BIGINT AUTO_INCREMENT COMMENT '商品详情ID',
    description TEXT COMMENT '商品描述（MarkDown）',
    status        smallint default 0 not null comment '0:正常,1:锁定',
    create_date   datetime           not null comment '创建时间',
    update_date   datetime           not null comment '更新时间',
    PRIMARY KEY (product_detail_id)
) COMMENT='商品详情表';


CREATE TABLE v_cart (
    cart_id BIGINT AUTO_INCREMENT COMMENT '购物车ID',
    user_id BIGINT NOT NULL COMMENT '用户ID，关联用户表',
    product_id BIGINT NOT NULL COMMENT '商品ID，关联商品表',
    quantity BIGINT NOT NULL COMMENT '数量',
    status        smallint default 0 not null comment '0:正常,1:锁定',
    create_date   datetime           not null comment '创建时间',
    update_date   datetime           not null comment '更新时间',
    PRIMARY KEY (cart_id)
) COMMENT='购物车表';

CREATE TABLE v_order (
    order_id BIGINT AUTO_INCREMENT COMMENT '订单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID关联用户表',
    total_price BIGINT NOT NULL COMMENT '订单总价',
    order_status smallint default 0 NOT NULL COMMENT '订单状态（状态机）',
    status        smallint default 0 not null comment '0:正常,1:锁定',
    create_date   datetime           not null comment '创建时间',
    update_date   datetime           not null comment '更新时间',
    PRIMARY KEY (order_id)
) COMMENT='下单表';



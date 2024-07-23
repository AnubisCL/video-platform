package com.example.videoweb.base.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

/**
 * 自定义元对象字段填充控制器，实现公共字段自动写入
 * @Author: chailei
 * @Date: 2024/4/11 13:56
 */
public class MysqlMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Object createDate = this.getFieldValByName("createDate", metaObject);
        if (createDate == null) {
            this.strictInsertFill(metaObject, "createDate", Date.class, new Date());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Object updateDate = this.getFieldValByName("updateDate", metaObject);
        if (updateDate == null) {
            this.strictUpdateFill(metaObject, "updateDate", Date.class, new Date());
        }
    }
}

package com.example.videoweb.dao;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.example.videoweb.domain.entity.VMenu;

/**
 * 菜单路由表(VMenu)表数据库访问层
 *
 * @author chailei
 * @since 2024-07-24 00:03:04
 */
public interface VMenuDao extends BaseMapper<VMenu> {

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<VMenu> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<VMenu> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<VMenu> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<VMenu> entities);

}


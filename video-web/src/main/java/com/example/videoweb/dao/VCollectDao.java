package com.example.videoweb.dao;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.example.videoweb.domain.entity.VCollect;

/**
 * 用户收藏表(VCollect)表数据库访问层
 *
 * @author chailei
 * @since 2024-07-23 17:29:15
 */
public interface VCollectDao extends BaseMapper<VCollect> {

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<VCollect> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<VCollect> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<VCollect> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<VCollect> entities);

}


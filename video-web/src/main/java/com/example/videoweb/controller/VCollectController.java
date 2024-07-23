package com.example.videoweb.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.videoweb.domain.entity.VCollect;
import com.example.videoweb.service.VCollectService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 用户收藏表(VCollect)表控制层
 *
 * @author chailei
 * @since 2024-07-23 17:29:15
 */
@RestController
@RequestMapping("vCollect")
public class VCollectController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private VCollectService vCollectService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param vCollect 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<VCollect> page, VCollect vCollect) {
        return success(this.vCollectService.page(page, new QueryWrapper<>(vCollect)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.vCollectService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param vCollect 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody VCollect vCollect) {
        return success(this.vCollectService.save(vCollect));
    }

    /**
     * 修改数据
     *
     * @param vCollect 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody VCollect vCollect) {
        return success(this.vCollectService.updateById(vCollect));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.vCollectService.removeByIds(idList));
    }
}


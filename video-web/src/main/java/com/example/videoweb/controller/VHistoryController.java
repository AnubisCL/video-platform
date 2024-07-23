package com.example.videoweb.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.videoweb.domain.entity.VHistory;
import com.example.videoweb.service.VHistoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * (VHistory)表控制层
 *
 * @author chailei
 * @since 2024-07-23 17:29:15
 */
@RestController
@RequestMapping("vHistory")
public class VHistoryController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private VHistoryService vHistoryService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param vHistory 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<VHistory> page, VHistory vHistory) {
        return success(this.vHistoryService.page(page, new QueryWrapper<>(vHistory)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.vHistoryService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param vHistory 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody VHistory vHistory) {
        return success(this.vHistoryService.save(vHistory));
    }

    /**
     * 修改数据
     *
     * @param vHistory 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody VHistory vHistory) {
        return success(this.vHistoryService.updateById(vHistory));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.vHistoryService.removeByIds(idList));
    }
}


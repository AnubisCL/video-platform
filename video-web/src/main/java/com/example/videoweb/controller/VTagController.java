package com.example.videoweb.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.videoweb.domain.entity.VTag;
import com.example.videoweb.service.VTagService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 视频标签表(VTag)表控制层
 *
 * @author chailei
 * @since 2024-07-23 17:29:16
 */
@RestController
@RequestMapping("vTag")
public class VTagController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private VTagService vTagService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param vTag 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<VTag> page, VTag vTag) {
        return success(this.vTagService.page(page, new QueryWrapper<>(vTag)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.vTagService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param vTag 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody VTag vTag) {
        return success(this.vTagService.save(vTag));
    }

    /**
     * 修改数据
     *
     * @param vTag 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody VTag vTag) {
        return success(this.vTagService.updateById(vTag));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.vTagService.removeByIds(idList));
    }
}


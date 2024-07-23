package com.example.videoweb.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.videoweb.domain.entity.VLike;
import com.example.videoweb.service.VLikeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 视频/评论/弹幕 点赞表(VLike)表控制层
 *
 * @author chailei
 * @since 2024-07-23 17:29:16
 */
@RestController
@RequestMapping("vLike")
public class VLikeController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private VLikeService vLikeService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param vLike 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<VLike> page, VLike vLike) {
        return success(this.vLikeService.page(page, new QueryWrapper<>(vLike)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.vLikeService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param vLike 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody VLike vLike) {
        return success(this.vLikeService.save(vLike));
    }

    /**
     * 修改数据
     *
     * @param vLike 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody VLike vLike) {
        return success(this.vLikeService.updateById(vLike));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.vLikeService.removeByIds(idList));
    }
}


package com.example.videoweb.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.videoweb.domain.entity.VReviewScrolling;
import com.example.videoweb.service.VReviewScrollingService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 视频评论/弹幕表(VReviewScrolling)表控制层
 *
 * @author chailei
 * @since 2024-07-23 17:29:16
 */
@RestController
@RequestMapping("vReviewScrolling")
public class VReviewScrollingController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private VReviewScrollingService vReviewScrollingService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param vReviewScrolling 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<VReviewScrolling> page, VReviewScrolling vReviewScrolling) {
        return success(this.vReviewScrollingService.page(page, new QueryWrapper<>(vReviewScrolling)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.vReviewScrollingService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param vReviewScrolling 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody VReviewScrolling vReviewScrolling) {
        return success(this.vReviewScrollingService.save(vReviewScrolling));
    }

    /**
     * 修改数据
     *
     * @param vReviewScrolling 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody VReviewScrolling vReviewScrolling) {
        return success(this.vReviewScrollingService.updateById(vReviewScrolling));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.vReviewScrollingService.removeByIds(idList));
    }
}


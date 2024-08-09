package com.example.videoweb.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.example.videoweb.domain.entity.Collect;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.service.ICollectService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 用户收藏表 前端控制器
 * </p>
 *
 * @author anubis
 * @since 2024-07-24
 */
@SaCheckLogin
@RestController
@RequestMapping("/collect/")
public class CollectController {

    @Resource ICollectService collectService;

    @GetMapping("do/{videoId}")
    public ResultVo doCollect(@PathVariable(value = "videoId") String videoId) {
        Long userId = StpUtil.getLoginIdAsLong();
        List<Collect> list = collectService.lambdaQuery()
                .eq(Collect::getVideoId, Long.valueOf(videoId))
                .eq(Collect::getUserId, userId).list();
        if (!list.isEmpty()) {
            collectService.lambdaUpdate().eq(Collect::getCollectId, list.get(0).getCollectId())
                    .eq(Collect::getVideoId, Long.valueOf(videoId)).eq(Collect::getUserId, userId)
                    .set(Collect::getStatus, StatusEnum.YES.getStatus())
                    .update();
        } else {
            Collect collect = new Collect();
            collect.setVideoId(Long.valueOf(videoId));
            collect.setUserId(userId);
            collectService.save(collect);
        }
        return ResultVo.data(!list.isEmpty());
    }

    @GetMapping("removeCollect/{videoId}")
    public ResultVo removeCollect(@PathVariable(value = "videoId") String videoId) {
        Long userId = StpUtil.getLoginIdAsLong();
        List<Collect> list = collectService.lambdaQuery().eq(Collect::getStatus, StatusEnum.YES.getStatus())
                .eq(Collect::getVideoId, Long.valueOf(videoId))
                .eq(Collect::getUserId, userId).list();
        if (!list.isEmpty()) {
            collectService.lambdaUpdate().eq(Collect::getVideoId, Long.valueOf(videoId))
                    .eq(Collect::getUserId, userId)
                    .set(Collect::getStatus, StatusEnum.NO.getStatus())
                    .update();
        }
        return ResultVo.data(!list.isEmpty());
    }

    @GetMapping("isCollect/{videoId}")
    public ResultVo isCollect(@PathVariable(value = "videoId") String videoId) {
        Long userId = StpUtil.getLoginIdAsLong();
        List<Collect> list = collectService.lambdaQuery().eq(Collect::getStatus, StatusEnum.YES.getStatus())
                .eq(Collect::getVideoId, Long.valueOf(videoId)).eq(Collect::getUserId, userId).list();
        return ResultVo.data(!list.isEmpty());
    }

}

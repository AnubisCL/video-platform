package com.example.videoweb.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.example.videoweb.domain.entity.Like;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.service.ILikeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 视频/评论/弹幕 点赞表 前端控制器
 * </p>
 *
 * @author anubis
 * @since 2024-07-24
 */
@SaCheckLogin
@RestController
@RequestMapping("/like/")
public class LikeController {

    @Resource ILikeService likeService;
    @GetMapping("do/{actionType}/{itemId}")
    public ResultVo doLike(
        @PathVariable(value = "actionType") String actionType,
        @PathVariable(value = "itemId") String itemId) {

        Long userId = StpUtil.getLoginIdAsLong();
        List<Like> list;

        // 根据 actionType 来选择查询条件
        if ("video".equals(actionType)) {
            list = likeService.lambdaQuery()
                    .eq(Like::getVideoId, Long.valueOf(itemId))
                    .eq(Like::getUserId, userId).list();
        } else if ("comment".equals(actionType)) {
            list = likeService.lambdaQuery()
                    .eq(Like::getCommentId, Long.valueOf(itemId))
                    .eq(Like::getUserId, userId).list();
        } else {
            // 处理无效的 actionType
            return ResultVo.error("Invalid action type");
        }

        if (!list.isEmpty()) {
            likeService.lambdaUpdate()
                    .eq(Like::getLikeId, list.get(0).getLikeId())
                    .eq(actionType.equals("video") ? Like::getVideoId : Like::getCommentId, Long.valueOf(itemId))
                    .eq(Like::getUserId, userId)
                    .set(Like::getStatus, StatusEnum.YES.getStatus())
                    .update();
        } else {
            Like like = new Like();
            if ("video".equals(actionType)) {
                like.setVideoId(Long.valueOf(itemId));
            } else {
                like.setCommentId(Long.valueOf(itemId));
            }
            like.setUserId(userId);
            likeService.save(like);
        }
        return ResultVo.data(!list.isEmpty());
    }

    @GetMapping("removeLike/{videoId}")
    public ResultVo removeLike(@PathVariable(value = "videoId") String videoId) {
        Long userId = StpUtil.getLoginIdAsLong();
        List<Like> list = likeService.lambdaQuery().eq(Like::getStatus, StatusEnum.YES.getStatus())
                .eq(Like::getVideoId, Long.valueOf(videoId))
                .eq(Like::getUserId, userId).list();
        if (!list.isEmpty()) {
            likeService.lambdaUpdate().eq(Like::getVideoId, Long.valueOf(videoId))
                    .eq(Like::getUserId, userId)
                    .set(Like::getStatus, StatusEnum.NO.getStatus())
                    .update();
        }
        return ResultVo.data(!list.isEmpty());
    }

    @GetMapping("isLike/{videoId}")
    public ResultVo isLike(@PathVariable(value = "videoId") String videoId) {
        Long userId = StpUtil.getLoginIdAsLong();
        List<Like> list = likeService.lambdaQuery().eq(Like::getStatus, StatusEnum.YES.getStatus())
                .eq(Like::getVideoId, Long.valueOf(videoId)).eq(Like::getUserId, userId).list();
        return ResultVo.data(!list.isEmpty());
    }

    @GetMapping("count/{videoId}")
    public ResultVo count(@PathVariable(value = "videoId") String videoId) {
        return ResultVo.data(likeService.lambdaQuery().eq(Like::getStatus, StatusEnum.YES.getStatus()).eq(Like::getVideoId, videoId).count());
    }

}

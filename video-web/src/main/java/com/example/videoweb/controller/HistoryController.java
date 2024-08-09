package com.example.videoweb.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.example.videoweb.domain.entity.History;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.service.IHistoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author anubis
 * @since 2024-07-24
 */
@SaCheckLogin
@RestController
@RequestMapping("/history/")
public class HistoryController {

    @Resource IHistoryService historyService;

    @GetMapping("do/{videoId}")
    public ResultVo doHistory(@PathVariable(value = "videoId") String videoId) {
        Long userId = StpUtil.getLoginIdAsLong();
        History histories = historyService.lambdaQuery()
                .eq(History::getStatus, StatusEnum.YES.getStatus())
                .eq(History::getVideoId, videoId)
                .eq(History::getUserId, userId).one();
        Optional.ofNullable(histories).ifPresentOrElse(history -> {
            historyService.lambdaUpdate().eq(History::getHistoryId, histories.getHistoryId())
                    .eq(History::getVideoId, videoId).eq(History::getUserId, userId)
                    .set(History::getCount, histories.getCount() + 1)
                    .set(History::getStatus, StatusEnum.YES.getStatus())
                    .update();
        }, () -> {
            History history = new History();
            history.setVideoId(Long.valueOf(videoId));
            history.setUserId(userId);
            historyService.save(history);
        });
        return ResultVo.ok();
    }

    @GetMapping("count/{videoId}")
    public ResultVo count(@PathVariable(value = "videoId") String videoId) {
        return ResultVo.data(historyService.lambdaQuery().eq(History::getStatus, StatusEnum.YES.getStatus()).eq(History::getVideoId, videoId).count());
    }

}

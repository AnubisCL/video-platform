package com.example.videoweb.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.videoweb.domain.dto.PageDto;
import com.example.videoweb.domain.dto.VideoDto;
import com.example.videoweb.domain.entity.Video;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.domain.vo.PageVo;
import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.domain.vo.VideoVo;
import com.example.videoweb.service.IVideoService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 视频表 前端控制器
 * </p>
 *
 * @author anubis
 * @since 2024-07-24
 */
@Valid
@SaCheckLogin
@RestController
@RequestMapping("/video/")
public class VideoController {

    @Resource private IVideoService videoService;

    @PostMapping("getVideoList")
    public ResultVo getVideoList(@Valid @RequestBody VideoDto videoDto) {
        PageDto pageDto = videoDto.getPage();
        Page<Video> page = Page.of(pageDto.getCurrent(), pageDto.getSize());
        if (pageDto.getSortBy().isEmpty()) {
            page.addOrder(OrderItem.desc("update_date"));
        } else {
            page.addOrder(pageDto.getAsc() ? OrderItem.asc(pageDto.getSortBy()) : OrderItem.desc(pageDto.getSortBy()));
        }
        // todo：VideoDto
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<Video>()
                .eq(Video::getStatus, StatusEnum.YES.getStatus());

        if (!StringUtils.isEmpty(videoDto.getKeyword())) {
            queryWrapper.like(Video::getTitle, videoDto.getKeyword()).or()
                    .like(Video::getDescription, videoDto.getKeyword());
        }

        Page<Video> resultPage = videoService.page(page, queryWrapper);
        List<VideoVo> records = resultPage.getRecords()
                .stream().map(video -> VideoVo.builder()
                        .videoId(video.getVideoId()).imageUrl(video.getImageUrl())
                        .videoUrl(video.getHlsUrl()).title(video.getTitle()).build()).toList();
        return ResultVo.data(PageVo.builder().pages(resultPage.getPages())
                .total(resultPage.getTotal()).records(records).build());
    }


}

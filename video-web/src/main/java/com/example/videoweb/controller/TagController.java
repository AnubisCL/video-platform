package com.example.videoweb.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.videoweb.domain.entity.Tag;
import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.service.IVideoTagService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 视频标签表 前端控制器
 * </p>
 *
 * @author anubis
 * @since 2024-07-24
 */
@Valid
@Slf4j
@SaCheckLogin
@RestController
@RequestMapping("/tag/")
public class TagController {

    @Resource private IVideoTagService videoTagService;

    @GetMapping("getTags/{videoId}")
    public ResultVo getTags(@PathVariable(value = "videoId") String videoId) {
        List<Tag> tags = videoTagService.getVideoTags(Long.valueOf(videoId));
        return ResultVo.data(tags);
    }
}

package com.example.videoweb.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.videoweb.domain.dto.PageDto;
import com.example.videoweb.domain.dto.VideoDto;
import com.example.videoweb.domain.entity.Collect;
import com.example.videoweb.domain.entity.History;
import com.example.videoweb.domain.entity.Like;
import com.example.videoweb.domain.entity.Video;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.domain.vo.PageVo;
import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.domain.vo.VideoVo;
import com.example.videoweb.service.ICollectService;
import com.example.videoweb.service.IHistoryService;
import com.example.videoweb.service.ILikeService;
import com.example.videoweb.service.IVideoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 视频表 前端控制器
 * </p>
 *
 * @author anubis
 * @since 2024-07-24
 */
@Valid
@Slf4j
@SaCheckLogin
@RestController
@RequestMapping("/video/")
public class VideoController {

    @Resource private IVideoService videoService;
    @Resource private ICollectService collectService;
    @Resource private ILikeService likeService;
    @Resource private IHistoryService historyService;

    @Value("${nginx-config.protocol-type.local.name}") private String local;
    @Value("${nginx-config.protocol-type.local.local-ipv4-url}") private String localIpv4Url;
    @Value("${nginx-config.protocol-type.ipv4.name}") private String ipv4;
    @Value("${nginx-config.protocol-type.ipv4.replace-ipv4}") private String replaceIpv4;
    @Value("${nginx-config.protocol-type.ipv6.name}") private String ipv6;
    @Value("${nginx-config.protocol-type.ipv6.replace-ipv6}") private String replaceIpv6;
    //todo：
    // 1.自动获取内网Ipv4地址，
    //  1.1. proot-distro ubuntu 可以获取到 ifconfig - wlan0 - inet
    // 2.自动获取内网Ipv6地址
    //  2.1. proot-distro ubuntu 获取不到 使用 curl 请求外部获取Ipv6
    // 缓存到本地，每隔10分钟更新一次缓存，
    // 3.Caffeine/Ehcache(支持磁盘持久化) 缓存到本地，每隔10分钟更新一次缓存

    @PostMapping("getVideoList")
    public ResultVo getVideoList(@Valid @RequestBody VideoDto videoDto, HttpServletRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        PageDto pageDto = videoDto.getPage();
        Page<Video> page = Page.of(pageDto.getCurrent(), pageDto.getSize());
        if (pageDto.getSortBy().isEmpty()) {
            page.addOrder(OrderItem.descs("video_id","update_date"));
        } else {
            page.addOrder(pageDto.getAsc() ? OrderItem.ascs("video_id", pageDto.getSortBy()) : OrderItem.descs("video_id", pageDto.getSortBy()));
        }

        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<Video>()
                .eq(Video::getStatus, StatusEnum.YES.getStatus());
        //关键词搜索
        if (!StringUtils.isEmpty(videoDto.getKeyword())) {
            queryWrapper.like(Video::getTitle, videoDto.getKeyword()).or()
                    .like(Video::getDescription, videoDto.getKeyword());
        }
        //收藏
        Optional.ofNullable(videoDto.getIsCollect()).ifPresent(isCollect -> {
            if (isCollect) {
                List<Collect> collects = collectService.lambdaQuery().eq(Collect::getStatus, StatusEnum.YES.getStatus())
                        .eq(Collect::getUserId, userId).list();
                queryWrapper.in(Video::getVideoId, collects.stream().map(Collect::getVideoId));
            }
        });
        //喜欢
        Optional.ofNullable(videoDto.getIsLike()).ifPresent(isLike -> {
            if (isLike) {
                List<Like> likes = likeService.lambdaQuery().eq(Like::getStatus, StatusEnum.YES.getStatus())
                        .eq(Like::getUserId, userId).list();
                queryWrapper.in(Video::getVideoId, likes.stream().map(Like::getVideoId));
            }
        });
        //历史记录
        Optional.ofNullable(videoDto.getIsHistory()).ifPresent(isHistory -> {
            if (isHistory) {
                List<History> histories = historyService.lambdaQuery().eq(History::getStatus, StatusEnum.YES.getStatus())
                        .eq(History::getUserId, userId).list();
                queryWrapper.in(Video::getVideoId, histories.stream().map(History::getVideoId));
            }
        });

        String protocolType = getIpAddressProtocolType(request);

        Page<Video> resultPage = videoService.page(page, queryWrapper);
        List<VideoVo> records = resultPage.getRecords()
                .stream().map(video -> {
                    VideoVo.VideoVoBuilder builder = VideoVo.builder();
                    builder.videoId(video.getVideoId()).title(video.getTitle());
                    if (protocolType.equals(ipv6)) {
                        builder.imageUrl(video.getImageUrl().replace(localIpv4Url, replaceIpv6))
                                .videoUrl(video.getHlsUrl().replace(localIpv4Url, replaceIpv6));
                    } else if (protocolType.equals(ipv4)) {
                        builder.imageUrl(video.getImageUrl().replace(localIpv4Url, replaceIpv4))
                                .videoUrl(video.getHlsUrl().replace(localIpv4Url, replaceIpv4));
                    } else if (protocolType.equals(local)) {
                        builder.imageUrl(video.getImageUrl()).videoUrl(video.getHlsUrl());
                    }
                    return builder.build();
                }).toList();
        return ResultVo.data(PageVo.builder().pages(resultPage.getPages())
                .total(resultPage.getTotal()).records(records).build());
    }

    private String getIpAddressProtocolType(HttpServletRequest request) {
        boolean isLocal = false;
        boolean isIpv4 = false;
        boolean isIpv6 = false;
        String host = request.getHeader("host");
        //ipv6
        String replace = host.replace("[", "").replace("]", "");
        // 判断host是ipv4还是ipv6还是127.0.0.1/localhost
        // 使用 InetAddress 来判断 IP 地址类型
        try {
            InetAddress inetAddress = InetAddress.getByName(replace); // 去掉可能存在的端口号
            if (inetAddress.isLoopbackAddress()) {
                isLocal = true;
            } else if (inetAddress instanceof java.net.Inet4Address) {
                isIpv4 = true;
            } else if (inetAddress instanceof java.net.Inet6Address) {
                isIpv6 = true;
            }
        } catch (UnknownHostException e) {
            // 如果 host 不是一个 IP 地址，尝试使用正则表达式判断
            if ("localhost".equalsIgnoreCase(replace)) {
                log.info("Host is localhost.");
                isLocal = true;
            } else if (replace.matches("^([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})$")) {
                isIpv4 = true;
            } else if (replace.matches("^([0-9a-fA-F]{0,4}:){2,7}[0-9a-fA-F]{0,4}$")) {
                isIpv6 = true;
            } else {
                log.warn("Host is not recognized as an IP address or localhost.");
            }
        }
        log.info("--- isLocal:{}, , isIpv4: {}, isIpv6:{}", isLocal, isIpv4, isIpv6);
        if (isIpv4) return "ipv4";
        else if (isIpv6) return "ipv6";
        else return "local";
    }


}

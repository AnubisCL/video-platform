package com.example.videoweb.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.videoweb.base.annotation.ReplaceIpFun;
import com.example.videoweb.base.config.CacheConfig;
import com.example.videoweb.base.properties.BaseDirectoryProperties;
import com.example.videoweb.domain.cache.IpInfo;
import com.example.videoweb.domain.dto.PageDto;
import com.example.videoweb.domain.dto.VideoDto;
import com.example.videoweb.domain.entity.*;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.domain.vo.PageVo;
import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.domain.vo.UploadFileVo;
import com.example.videoweb.domain.vo.VideoVo;
import com.example.videoweb.service.*;
import com.example.videoweb.utils.IpUtil;
import com.example.videoweb.utils.ProcessUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
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
    @Resource private IFileInfoService fileInfoService;
    @Resource private BaseDirectoryProperties baseDirectoryProperties;
    @Resource @Qualifier("ehCacheManager") private CacheManager cacheManager;

    @Value("${nginx-config.protocol-type.local.name}") private String local;
    @Value("${nginx-config.protocol-type.local.local-ipv4-ip}") private String localIp;
    @Value("${nginx-config.protocol-type.ipv4.name}") private String ipv4;
    @Value("${nginx-config.protocol-type.ipv6.name}") private String ipv6;
    @Value("${nginx-config.protocol-type.domain.name}") private String domain;
    @Value("${nginx-config.protocol-type.domain.host}") private String domainHost;
    @Value("${nginx-config.file-suffix}") private String fileSuffix;

    private static HashSet<String> fileTypeList;
    static {
        fileTypeList = new HashSet<String>();
        fileTypeList.add("mp4");
        fileTypeList.add("gif");
        fileTypeList.add("pdf");
        fileTypeList.add("docx");
        fileTypeList.add("xlsx");
        fileTypeList.add("md");
        fileTypeList.add("png");
        fileTypeList.add("jpg");
        fileTypeList.add("jpeg");
    }


    @ReplaceIpFun
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

        String protocolType = IpUtil.getIpAddressProtocolType(request, domainHost);
        IpInfo ipInfo = cacheManager.getCache(CacheConfig.IP_CACHE_NAME, String.class, IpInfo.class).get(CacheConfig.IP_CACHE_NAME);

        Page<Video> resultPage = videoService.page(page, queryWrapper);
        List<VideoVo> records = resultPage.getRecords()
                .stream().map(video -> VideoVo.builder()
                        .videoId(video.getVideoId())
                        .title(video.getTitle())
                        .imageUrl(video.getImageUrl())
                        .videoUrl(video.getHlsUrl())
                        .build())
                .toList();
        return ResultVo.data(PageVo.builder().pages(resultPage.getPages())
                .total(resultPage.getTotal()).records(records).build());
    }

    @ReplaceIpFun
    @PostMapping("uploadFile")
    public ResultVo uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        FileInfo fileInfo = new FileInfo();
        fileInfo.setUserId(userId);
        fileInfoService.save(fileInfo);
        Long fileId = fileInfo.getFileId();
        log.info("文件Id：" + fileId);
        String originalFilename = file.getOriginalFilename();
        fileInfo.setFileName(originalFilename);
        log.info("文件名：" + originalFilename);
        String[] split = originalFilename.split("\\.");
        String fileType = split[split.length - 1];
        log.info("文件格式：" + fileType);
        if (!fileTypeList.contains(fileType)) {
            return ResultVo.error("文件格式不允许上传");
        }

        try {
            int length = file.getBytes().length;
            log.info("文件大小：" + String.valueOf(length/1000) + "KB");
            fileInfo.setFileSize(String.valueOf(length/1000) + "KB");
            if (length > 100000000) { //100M
                return ResultVo.error("文件太大不允许上传");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            fileInfoService.removeById(fileId);
            return ResultVo.error("文件格式异常不允许上传");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String dateStr = LocalDate.now().format(formatter);
        String dataPath = ProcessUtil.createDateDirectory(baseDirectoryProperties.getBackFile(), dateStr);
        String filePath = dataPath + File.separator + fileId + "." + fileType;
        log.info("文件filePath：" + filePath);
        fileInfo.setFilePath(filePath);
        String fileUrl = filePath.replace(baseDirectoryProperties.getBackFile(), fileSuffix);
        log.info("文件fileUrl：" + fileUrl);
        fileInfo.setFileUrl(fileUrl);
        File saveFile = new File(filePath);
        try {
            file.transferTo(saveFile);
            fileInfoService.updateById(fileInfo);
        } catch (IOException e) {
            fileInfoService.removeById(fileId);
            log.error(e.getMessage());
            return ResultVo.error("文件上传失败");
        }
        UploadFileVo uploadFileVo = new UploadFileVo();
        uploadFileVo.setFileUrl(fileInfo.getFileUrl());
        uploadFileVo.setReplaceFileUrl(fileInfo.getFileUrl());
        return ResultVo.data(uploadFileVo);
    }


}

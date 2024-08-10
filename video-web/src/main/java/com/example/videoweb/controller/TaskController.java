package com.example.videoweb.controller;

import com.alibaba.fastjson.JSON;
import com.example.videoweb.base.config.CacheConfig;
import com.example.videoweb.domain.cache.IpInfo;
import com.example.videoweb.domain.entity.Task;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.domain.enums.TaskStatusEnum;
import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.schedule.IpInfoSchedule;
import com.example.videoweb.schedule.TaskSchedule;
import com.example.videoweb.service.ITaskService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 任务表 前端控制器
 * </p>
 *
 * @author anubis
 * @since 2024-07-30
 */
@Slf4j
@RestController
@RequestMapping("/task/")
public class TaskController {

    @Resource private ITaskService taskService;
    @Resource private TaskSchedule taskSchedule;
    @Resource private IpInfoSchedule ipInfoSchedule;
    @Resource @Qualifier("ehCacheManager") private CacheManager cacheManager;

    @PostMapping("/insertTaskTxt")
    public ResultVo insertTaskTxt(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResultVo.error("文件未上传");
        }
        List<Task> taskList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split("\\|");
                if (split.length == 4) {
                    String title = split[0];
                    String infoUrl = split[1];
                    String imageUrl = split[2];
                    String m3u8Url = split[3];
                    Task task = new Task();
                    Map<String, String> jsonMap = new HashMap<>();
                    jsonMap.put("infoUrl", infoUrl);
                    jsonMap.put("imageUrl", imageUrl);
                    task.setTaskName(title);
                    task.setDownloadUrl(m3u8Url);
                    task.setTaskType("m3u8");
                    task.setDownloadJson(JSON.toJSONString(jsonMap));
                    task.setStatus(StatusEnum.YES.getStatus());
                    task.setTaskStatus(TaskStatusEnum.UN_START.getCode());
                    taskList.add(task);
                    log.info("任务：{}", taskList.size());
                } else {
                    log.info("任务：{} - {}", taskList.size(), line);
                }
            }
            taskService.saveBatch(taskList);
            return ResultVo.ok("文件上传成功");
        } catch (IOException e) {
            return ResultVo.error("文件上传失败：" + e.getMessage());
        }
    }

    @GetMapping("/doDownloadVideo")
    public ResultVo downloadVideo() {
        taskSchedule.downloadVideoSchedule();
        return ResultVo.ok("执行成功");
    }

    @GetMapping("/doPushHlsVideoStreams")
    public ResultVo pushHlsVideoStreams() {
        taskSchedule.pushHlsVideoStreamsSchedule();
        return ResultVo.ok("执行成功");
    }

    @GetMapping("/updateIpv4AndIpv6Schedule")
    public ResultVo updateIpv4AndIpv6Schedule() {
        ipInfoSchedule.updateIpv4AndIpv6Schedule();
        IpInfo info = cacheManager.getCache(CacheConfig.IP_CACHE_NAME, String.class, IpInfo.class).get(CacheConfig.IP_CACHE_NAME);
        return ResultVo.data(JSON.toJSONString(info));
    }




}

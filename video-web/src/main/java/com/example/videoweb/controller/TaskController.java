package com.example.videoweb.controller;

import com.alibaba.fastjson.JSON;
import com.example.videoweb.domain.entity.Task;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.domain.enums.TaskStatusEnum;
import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.service.ITaskService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

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

}

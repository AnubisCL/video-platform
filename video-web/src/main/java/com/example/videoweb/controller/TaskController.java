package com.example.videoweb.controller;

import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.service.ITaskService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <p>
 * 任务表 前端控制器
 * </p>
 *
 * @author anubis
 * @since 2024-07-30
 */
@RestController
@RequestMapping("/task/")
public class TaskController {

    @Resource private ITaskService taskService;

    @PostMapping("/insertTaskTxt")
    public ResultVo insertTaskTxt(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResultVo.error("文件未上传");
        }
        try {
            // 获取文件名
            String fileName = file.getOriginalFilename();
            // 指定文件保存目录
            //"/Users/anubis/IdeaProjects/MyGitHub/video-plaform"
            Path uploadPath = Paths.get(System.getProperty("user.dir"));
            // 将文件保存到指定目录
            Files.copy(file.getInputStream(), uploadPath.resolve(fileName));
            // todo: 导入txt到task表
            // 这里可以添加解析文件并将数据导入数据库的逻辑

            return ResultVo.ok("文件上传成功");
        } catch (IOException e) {
            return ResultVo.error("文件上传失败：" + e.getMessage());
        }
    }

}

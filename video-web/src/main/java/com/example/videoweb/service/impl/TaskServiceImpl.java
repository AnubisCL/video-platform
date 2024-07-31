package com.example.videoweb.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.videoweb.base.utils.ProcessUtil;
import com.example.videoweb.domain.entity.Task;
import com.example.videoweb.domain.enums.TaskStatusEnum;
import com.example.videoweb.mapper.TaskMapper;
import com.example.videoweb.service.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 任务表 服务实现类
 * </p>
 *
 * @author anubis
 * @since 2024-07-30
 */
@Slf4j
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements ITaskService {

    @Value("${directory.back-video}")
    private String BASE_DIR;

    @Override
    public void downloadVideo(Task task) {
        task.setTaskStatus(TaskStatusEnum.DOWNLOADING.getCode());
        baseMapper.updateById(task);

        String path = createDateDirectory(BASE_DIR, LocalDate.now());
        String videoOutputPath = path + File.separator + task.getTaskId() + ".mp4";
        String gifOutputPath = path + File.separator + task.getTaskId() + ".gif";

        // Download video using ffmpeg
        boolean downloadSuccess = ProcessUtil.executeCommand(
                Arrays.asList("ffmpeg", "-i", task.getDownloadUrl(), "-c", "copy", videoOutputPath)
        );
        if (!downloadSuccess) {
            log.error("Failed to download the video.");
            task.setTaskStatus(TaskStatusEnum.DOWNLOAD_FAIL.getCode());
        }

        // Get video duration using ffprobe
        double totalSeconds = Double.parseDouble(ProcessUtil.executeCommandWithResult(
                Arrays.asList("ffprobe", "-v", "error", "-show_entries", "format=duration", "-of",
                        "default=noprint_wrappers=1:nokey=1", videoOutputPath)
        ));

        // Generate GIF thumbnail using ffmpeg
        boolean gifSuccess = ProcessUtil.executeCommand(
                Arrays.asList("ffmpeg", "-ss", Integer.toString((int) (totalSeconds / 2)),
                        "-i", videoOutputPath, "-vframes", "125", "-y", gifOutputPath));
        if (!gifSuccess) {
            log.error("Failed to generate GIF thumbnail.");
        }
        updateTaskWithResults(task, totalSeconds, gifOutputPath, videoOutputPath);
    }

    @Override
    public void pushHlsVideoStreams(Task task) {

    }

    private void updateTaskWithResults(Task task, double totalSeconds, String gifOutputPath, String videoOutputPath) {
        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("total_seconds", String.valueOf(totalSeconds));
        jsonMap.put("frame", "25");
        jsonMap.put("output_gif_path", gifOutputPath);
        jsonMap.put("output_video_path", videoOutputPath);

        JSONObject json = JSON.parseObject(task.getDownloadJson());
        json.put("download_res", jsonMap);
        task.setDownloadJson(json.toJSONString());
        task.setTaskStatus(TaskStatusEnum.DOWNLOAD_COMPLETE.getCode());
        baseMapper.updateById(task);
    }

    public static String createDateDirectory(String baseDir, LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String dateStr = date.format(formatter);
        String fullDirPath = baseDir + dateStr;
        Path path = Paths.get(fullDirPath);
        try {
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            Files.createDirectory(path); // 注意这里改为单个目录创建
            log.info("Directory created: " + fullDirPath);
        } catch (IOException e) {
            log.error("Error creating directory: " + e.getMessage());
        }
        return fullDirPath;
    }
}


package com.example.videoweb.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.videoweb.base.utils.ProcessUtil;
import com.example.videoweb.domain.entity.Task;
import com.example.videoweb.domain.entity.Video;
import com.example.videoweb.domain.enums.TaskStatusEnum;
import com.example.videoweb.mapper.TaskMapper;
import com.example.videoweb.service.ITaskService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Value("${directory.back-video}") private String BASE_DIR;
    @Value("${nginx-config.m3u8-suffix}") private String m3u8Suffix;
    @Value("${nginx-config.mp4-suffix}") private String mp4Suffix;
    @Value("${nginx-config.gif-suffix}") private String gifSuffix;
    @Value("${ffmpeg.log-level}") private String ffmpegLogLevel;
    @Resource private VideoServiceImpl videoService;

    @Override
    @Transactional
    public void downloadVideo(Task task) {
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
        //boolean gifSuccess = ProcessUtil.executeCommand(
        //        Arrays.asList("ffmpeg", "-ss", Integer.toString((int) (totalSeconds / 2)),
        //                "-i", videoOutputPath, "-vframes", "125", "-y", gifOutputPath));
        //前5秒生成GIF
        boolean gifSuccess = ProcessUtil.executeCommand(
                Arrays.asList("ffmpeg", "-loglevel", ffmpegLogLevel, "-i", videoOutputPath, "-t", "5", "-pix_fmt", "rgb24", gifOutputPath));
        if (!gifSuccess) {
            log.error("Failed to generate GIF thumbnail.");
        }
        if (!gifSuccess) {
            log.error("Failed to generate GIF thumbnail.");
        }
        updateTaskWithResults(task, totalSeconds, gifOutputPath, videoOutputPath);
    }

    @Override
    @Transactional
    public void pushHlsVideoStreams(Task task) {
        Task updateTask = new Task();
        updateTask.setTaskId(task.getTaskId());
        updateTask.setTaskStatus(TaskStatusEnum.MP4_COMPLETE.getCode());
        baseMapper.updateById(updateTask);

        Video video = new Video();
        video.setTitle(task.getTaskName());
        video.setSubheading("");
        video.setVideoSet(0L);
        video.setVideoSetName("第一集");
        String downloadJson = task.getDownloadJson();
        JSONObject jsonObject = JSON.parseObject(downloadJson);
        JSONObject downloadRes = jsonObject.getJSONObject("download_res");
        String outputGifPath = downloadRes.getString("output_gif_path");
        String outputVideoPath = downloadRes.getString("output_video_path");
        String totalSeconds = downloadRes.getString("total_seconds");
        String frame = downloadRes.getString("frame");
        video.setDescription("时长: " + formatSecondsToMinutesAndSeconds(Double.parseDouble(totalSeconds)) + "，帧数: " + frame);
        String replaceVideoPath = outputVideoPath.replace(BASE_DIR, mp4Suffix);
        String replaceGifPath = outputGifPath.replace(BASE_DIR, gifSuffix);
        video.setHlsUrl(replaceVideoPath); // fixme：推流m3u8
        video.setImageUrl(replaceGifPath);
        videoService.save(video);
    }

    public static String formatSecondsToMinutesAndSeconds(double seconds) {
        int minutes = (int) (seconds / 60);
        int secs = (int) (seconds % 60);
        return String.format("%d:%02d", minutes, secs);
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


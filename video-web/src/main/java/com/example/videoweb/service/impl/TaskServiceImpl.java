package com.example.videoweb.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.videoweb.base.properties.BaseDirectoryProperties;
import com.example.videoweb.utils.ProcessUtil;
import com.example.videoweb.domain.entity.Task;
import com.example.videoweb.domain.entity.Video;
import com.example.videoweb.domain.enums.TaskStatusEnum;
import com.example.videoweb.mapper.TaskMapper;
import com.example.videoweb.service.ITaskService;
import com.example.videoweb.service.IVideoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

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

    private static final String INDEX_M3U8 = "index.m3u8";
    @Resource private BaseDirectoryProperties baseDirectoryProperties;
    @Value("${nginx-config.m3u8-suffix}") private String m3u8Suffix;
    @Value("${nginx-config.mp4-suffix}") private String mp4Suffix;
    @Value("${nginx-config.gif-suffix}") private String gifSuffix;
    @Value("${ffmpeg.log-level}") private String ffmpegLogLevel;
    @Value("${ffmpeg.hls-time}") private String ffmpegHlsTime;
    @Resource private IVideoService videoService;
    @Resource private TaskMapper taskMapper;

    @Resource private PlatformTransactionManager transactionManager;

    @Override
    @Transactional
    public void downloadVideo(Task task) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String dateStr = LocalDate.now().format(formatter);
        String path = createDateDirectory(baseDirectoryProperties.getBackVideo(), dateStr);
        String videoOutputPath = path + File.separator + task.getTaskId() + ".mp4";
        String gifOutputPath = path + File.separator + task.getTaskId() + ".gif";

        // Download video using ffmpeg
        boolean downloadSuccess = ProcessUtil.executeCommand(
                Arrays.asList("ffmpeg", "-i", task.getDownloadUrl(), "-c", "copy", videoOutputPath)
        );
        if (!downloadSuccess) {
            log.error("Failed to download the video.");
            Task errorTask = new Task();
            errorTask.setTaskId(task.getTaskId());
            errorTask.setTaskStatus(TaskStatusEnum.DOWNLOAD_FAIL.getCode());
            taskMapper.updateById(errorTask);
            return;
        }
        log.info("download the video success.");

        // Get video duration using ffprobe
        double totalSeconds = Double.parseDouble(ProcessUtil.executeCommandWithResult(
                Arrays.asList("ffprobe", "-v", "error", "-show_entries", "format=duration", "-of",
                        "default=noprint_wrappers=1:nokey=1", videoOutputPath)
        ));
        log.info("get video totalSeconds success : {}", totalSeconds);

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
        log.info("Generate GIF success .");

        updateTaskWithResults(task, totalSeconds, gifOutputPath, videoOutputPath);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pushMp4VideoStreams(Task task) {
        Task updateTask = new Task();
        updateTask.setTaskId(task.getTaskId());
        updateTask.setTaskStatus(TaskStatusEnum.MP4_COMPLETE.getCode());
        taskMapper.updateById(updateTask);

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
        String replaceVideoPath = outputVideoPath.replace(baseDirectoryProperties.getBackVideo(), mp4Suffix);
        String replaceGifPath = outputGifPath.replace(baseDirectoryProperties.getBackVideo(), gifSuffix);
        video.setHlsUrl(replaceVideoPath);
        video.setImageUrl(replaceGifPath);
        videoService.save(video);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean pushHlsVideoStreams(Task task) {
        boolean result = true;
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
        String replaceGifPath = outputGifPath.replace(baseDirectoryProperties.getBackVideo(), gifSuffix);
        video.setImageUrl(replaceGifPath);
        video.setHlsUrl("");
        String loadDirectory = null;

        Task updateTask = new Task();
        updateTask.setTaskId(task.getTaskId());

        try {
            videoService.save(video);

            loadDirectory = createDateDirectory(baseDirectoryProperties.getHlsVideo(), String.valueOf(video.getVideoId()));
            String replaceVideoPath = m3u8Suffix + video.getVideoId() + File.separator + INDEX_M3U8;
            Video videoUpdate = new Video();
            videoUpdate.setVideoId(video.getVideoId());
            videoUpdate.setHlsUrl(replaceVideoPath);
            videoService.updateById(videoUpdate);

            //ffmpeg -i input.mp4 -c:v libx264 -c:a aac -strict -2 -f hls -hls_time 20 -hls_list_size 0 -hls_wrap 0 output.m3u8
            //ffmpeg -i input.mp4 -hls_time 10 -hls_list_size 0 -hls_segment_filename output_%03d.ts output.m3u8
            boolean executeCommand = ProcessUtil.executeCommand(
                    Arrays.asList("ffmpeg", "-i", outputVideoPath, "-c:v", "libx264", "-c:a", "aac", "-strict",
                            "-2", "-f", "hls", "-hls_time", ffmpegHlsTime, "-hls_list_size", "0", loadDirectory + File.separator + INDEX_M3U8)
            ); //"-hls_wrap", "0",

            if (executeCommand) {
                updateTask.setTaskStatus(TaskStatusEnum.PUSH_COMPLETE.getCode());
            } else {
                updateTask.setTaskStatus(TaskStatusEnum.PUSH_FAIL.getCode());
                throw new RuntimeException("Failed to push video stream.");
            }
        } catch (Exception e) {
            result = false;
            log.error("pushHlsVideoStreams error : {}", e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("clean loadDirectory : {}", loadDirectory);
            cleanLoadDirectory(loadDirectory);
        } finally {
            taskMapper.updateById(updateTask);
        }
        log.info("pushHlsVideoStreams finished result : [taskId:{},result:{}]", task.getTaskId(), result);
        return result;
    }

    private static void cleanLoadDirectory(String loadDirectory) {
        //删除一个目录
        try {
            FileUtils.deleteDirectory(new File(loadDirectory));
        } catch (IOException e) {
            log.error("Error cleanLoadDirectory : " + e.getMessage());
        }
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
        taskMapper.updateById(task);
    }

    public static String createDateDirectory(String baseDir, String dirStr) {
        String fullDirPath = baseDir + dirStr;
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


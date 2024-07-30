package com.example.videoweb.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.videoweb.domain.entity.Task;
import com.example.videoweb.domain.enums.TaskStatusEnum;
import com.example.videoweb.mapper.TaskMapper;
import com.example.videoweb.service.ITaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

    private static final String BASE_DIR = "/home/back/video/";

    @Override
    public void downloadVideo(Task task) {
        String path = createDateDirectory(BASE_DIR, LocalDate.now());
        String videoOutputPath = path + File.separator + task.getTaskName() + ".mp4";
        String gifOutputPath = path + File.separator + task.getTaskName() + ".gif";

        // Download video using ffmpeg
        boolean downloadSuccess = executeCommand(
                Arrays.asList("ffmpeg", "-i", task.getDownloadUrl(), "-c", "copy", videoOutputPath)
        );
        if (!downloadSuccess) {
            log.error("Failed to download the video.");
            task.setTaskStatus(TaskStatusEnum.DOWNLOAD_FAIL.getCode());
        }

        // Get video duration using ffprobe
        double totalSeconds = executeCommandWithResult(
                Arrays.asList("ffprobe", "-v", "error", "-show_entries", "format=duration", "-of",
                        "default=noprint_wrappers=1:nokey=1", videoOutputPath)
        );

        // Generate GIF thumbnail using ffmpeg
        boolean gifSuccess = executeCommand(
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

    private boolean executeCommand(List<String> command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                readAndLogErrorStream(process);
                return false;
            }
            return true;
        } catch (IOException | InterruptedException e) {
            log.error("Error executing command: {}", e.getMessage());
            return false;
        }
    }

    private double executeCommandWithResult(List<String> command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            reader.close();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                readAndLogErrorStream(process);
                throw new RuntimeException("Command failed with exit code: " + exitCode);
            }
            return Double.parseDouble(output.toString().trim());
        } catch (IOException | InterruptedException | NumberFormatException e) {
            log.error("Error executing command or parsing result: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void readAndLogErrorStream(Process process) throws IOException {
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder errorOutput = new StringBuilder();
        String line;
        while ((line = errorReader.readLine()) != null) {
            errorOutput.append(line).append("\n");
        }
        errorReader.close();
        log.error("Error output: {}", errorOutput.toString());
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
        try {
            Files.createDirectories(Paths.get(fullDirPath));
            log.info("Directory created: " + fullDirPath);
        } catch (IOException e) {
            log.error("Error creating directory: " + e.getMessage());
        }
        return fullDirPath;
    }
}


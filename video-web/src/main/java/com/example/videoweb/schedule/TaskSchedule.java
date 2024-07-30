package com.example.videoweb.schedule;

import com.example.videoweb.domain.entity.Task;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.domain.enums.TaskStatusEnum;
import com.example.videoweb.service.ITaskService;
import com.example.videoweb.service.IVideoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: chailei
 * @Date: 2024/7/30 10:10
 */
@Slf4j
@Component
public class TaskSchedule {

    @Resource private ThreadPoolTaskExecutor videoExecutor;
    @Resource private IVideoService videoService;
    @Resource private ITaskService taskService;


    @Scheduled(cron = "${schedule.cron.downloadVideo}")
    public void downloadVideoSchedule() {
        log.info(" --- downloadVideoSchedule start --- ");
        List<Task> taskList = taskService.lambdaQuery()
                .eq(Task::getStatus, StatusEnum.YES.getStatus())
                .eq(Task::getTaskStatus, TaskStatusEnum.UN_START).list();
        taskList.forEach(task -> {
            videoExecutor.execute(() -> {
                taskService.downloadVideo(task);
            });
        });
        log.info(" --- downloadVideoSchedule end --- ");
    }

    @Scheduled(cron = "${schedule.cron.pushHlsVideoStreams}")
    public void pushHlsVideoStreamsSchedule() {
        log.info(" --- pushHlsVideoStreamsSchedule start --- ");
        List<Task> taskList = taskService.lambdaQuery()
                .eq(Task::getStatus, StatusEnum.YES.getStatus())
                .eq(Task::getTaskStatus, TaskStatusEnum.DOWNLOAD_COMPLETE).list();
        taskList.forEach(task -> {
            videoExecutor.execute(() -> {
                taskService.pushHlsVideoStreams(task);
            });
        });
        log.info(" --- pushHlsVideoStreamsSchedule end --- ");
    }

    @Scheduled(cron = "${schedule.cron.cleanDownloadVideo}")
    public void cleanDownloadVideoSchedule() {
        log.info(" --- cleanDownloadVideoSchedule start --- ");
        //todo：del /temp/video
        log.info(" --- cleanDownloadVideoSchedule end --- ");
    }

}

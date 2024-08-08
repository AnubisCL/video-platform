package com.example.videoweb.schedule;

import com.example.videoweb.domain.entity.Task;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.domain.enums.TaskStatusEnum;
import com.example.videoweb.service.ITaskService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: anubis
 * @Date: 2024/7/30 10:10
 */
@Slf4j
@Component
public class TaskSchedule {

    @Resource private ThreadPoolTaskExecutor videoExecutor;
    @Resource private ITaskService taskService;


    @Async
    @Scheduled(cron = "${schedule.cron.downloadVideo}")
    public void downloadVideoSchedule() {
        log.info(" --- downloadVideoSchedule start --- ");
        List<Task> taskList = taskService.lambdaQuery()
                .eq(Task::getStatus, StatusEnum.YES.getStatus())
                .eq(Task::getTaskStatus, TaskStatusEnum.UN_START.getCode())
                .orderByDesc(Task::getUpdateDate)
                .last("limit 1")
                .list();
        taskList.forEach(task -> {
            videoExecutor.execute(() -> {
                Task updateTask = new Task();
                updateTask.setTaskId(task.getTaskId());
                updateTask.setTaskStatus(TaskStatusEnum.DOWNLOADING.getCode());
                taskService.updateById(updateTask);
                taskService.downloadVideo(task);
            });
        });
        log.info(" --- downloadVideoSchedule end --- ");
    }

    private final ReentrantLock lock = new ReentrantLock();

    @Async
    @Scheduled(cron = "${schedule.cron.pushHlsVideoStreams}")
    public void pushHlsVideoStreamsSchedule() {
        try {
            if (!lock.tryLock(5, TimeUnit.SECONDS)) {
                log.error("Failed to acquire lock after 5 seconds, skipping this execution.");
                return;
            }
        } catch (InterruptedException e) {
            log.error("Failed to tryLock error : {}", e.getMessage());
            return;
        }
        try {
            log.info(" --- pushHlsVideoStreamsSchedule start --- ");
            List<Task> taskList = taskService.lambdaQuery()
                    .eq(Task::getStatus, StatusEnum.YES.getStatus())
                    .eq(Task::getTaskStatus, TaskStatusEnum.DOWNLOAD_COMPLETE.getCode())
                    .orderByDesc(Task::getUpdateDate)
                    .list();

            List<CompletableFuture<Boolean>> futures = taskList.stream()
                    .map(task -> CompletableFuture.supplyAsync(() -> {
                        Task updateTask = new Task();
                        updateTask.setTaskId(task.getTaskId());
                        updateTask.setTaskStatus(TaskStatusEnum.PUSHING.getCode());
                        taskService.updateById(updateTask);

                        return taskService.pushHlsVideoStreams(task);
                    }, videoExecutor))
                    .toList();

            // 等待所有任务完成
            // 当传递一个空数组 new CompletableFuture[0] 作为 toArray() 方法的参数时，它会创建一个新的数组，并将列表中的所有元素复制到这个新数组中。
            // 这个新数组的类型是由你提供的空数组的类型决定的。在这种情况下，由于你提供了 new CompletableFuture[0]，所以最终得到的数组将是 CompletableFuture<T>[] 类型。
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            log.info(" --- pushHlsVideoStreamsSchedule end --- ");
        } finally {
            lock.unlock();
        }
    }

    //@Scheduled(cron = "${schedule.cron.cleanDownloadVideo}")
    public void cleanDownloadVideoSchedule() {
        log.info(" --- cleanDownloadVideoSchedule start --- ");
        //todo：del /temp/video
        log.info(" --- cleanDownloadVideoSchedule end --- ");
    }

}

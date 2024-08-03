package com.example.videoweb.service;

import com.example.videoweb.domain.entity.Task;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 任务表 服务类
 * </p>
 *
 * @author anubis
 * @since 2024-07-30
 */
public interface ITaskService extends IService<Task> {

    void downloadVideo(Task task);

    void pushMp4VideoStreams(Task task);
    Boolean pushHlsVideoStreams(Task task);
}

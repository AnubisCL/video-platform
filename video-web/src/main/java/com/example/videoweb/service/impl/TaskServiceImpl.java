package com.example.videoweb.service.impl;

import com.example.videoweb.domain.entity.Task;
import com.example.videoweb.mapper.TaskMapper;
import com.example.videoweb.service.ITaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 任务表 服务实现类
 * </p>
 *
 * @author anubis
 * @since 2024-07-30
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements ITaskService {

}

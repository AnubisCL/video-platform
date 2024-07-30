package com.example.videoweb.controller;

import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.service.ITaskService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/insertTaskJson")
    public ResultVo insertTaskJson(@Parameter(description = "导入json") String json) {
        //todo：导入json到task表
        return ResultVo.ok("导入成功");
    }

}

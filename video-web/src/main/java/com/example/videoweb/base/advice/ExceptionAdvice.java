package com.example.videoweb.base.advice;

import cn.dev33.satoken.exception.NotLoginException;
import com.example.videoweb.domain.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: anubis
 * @Date: 2024/7/23 21:14
 */
@Slf4j
@ResponseBody
@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResultVo methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        Map<String, String> collect = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        log.error("[参数错误]MethodArgumentNotValidException: {}", collect.toString());
        return ResultVo.error("参数错误");
    }

    @ExceptionHandler(value = NotLoginException.class)
    public ResultVo methodArgumentNotValidExceptionHandler(NotLoginException e) {
        log.error("[参数错误]MethodArgumentNotValidException: {}", e.toString());
        return ResultVo.error("登录已失效");
    }

}

package com.example.videoweb.base.advice;

import com.alibaba.fastjson.JSON;
import com.example.videoweb.base.annotation.ApiEncrypt;
import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.service.IEncryptService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @Author: chailei
 * @Date: 2024/8/16 09:51
 */
@Slf4j
@ControllerAdvice
public class ResponseBodyEncryptAdvice implements ResponseBodyAdvice<ResultVo> {

    @Resource private IEncryptService encryptService;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 判断方法或类上使用了ApiEncrypt注解
        return returnType.hasMethodAnnotation(ApiEncrypt.class) || returnType.getContainingClass().isAnnotationPresent(ApiEncrypt.class);
    }

    @Override
    public ResultVo beforeBodyWrite(ResultVo body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body.getData() == null) {
            return body;
        }
        String result = encryptService.encrypt(JSON.toJSONString(body.getData()));
        body.setData(result);
        return body;
    }

}
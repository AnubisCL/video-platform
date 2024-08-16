package com.example.videoweb.base.advice;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.example.videoweb.base.annotation.ApiDecrypt;
import com.example.videoweb.domain.dto.ApiEncryptDto;
import com.example.videoweb.service.IEncryptService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * @Author: chailei
 * @Date: 2024/8/16 09:50
 */
@Slf4j
@ControllerAdvice
public class RequestBodyDecryptAdvice implements RequestBodyAdvice {

    /**
     * 编码格式
     */
    private static final String ENCODING = "UTF-8";

    @Resource private IEncryptService encryptService;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 判断方法或类上使用了ApiDecrypt注解
        return methodParameter.hasMethodAnnotation(ApiDecrypt.class) ||methodParameter.getContainingClass().isAnnotationPresent(ApiDecrypt.class);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        try {
            String body = IOUtils.toString(inputMessage.getBody(), ENCODING);
            ApiEncryptDto apiEncryptDTO = JSONObject.parseObject(body, ApiEncryptDto.class);
            if (StrUtil.isEmpty(apiEncryptDTO.getEncryptData())) {
                return inputMessage;
            }
            String decrypt = encryptService.decrypt(apiEncryptDTO.getEncryptData());
            return new DecryptHttpInputMessage(inputMessage.getHeaders(), IOUtils.toInputStream(decrypt, ENCODING));
        } catch (Exception e) {
            log.error("接口数据解密失败: {}", e.getMessage(), e);
            return inputMessage;
        }
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    static class DecryptHttpInputMessage implements HttpInputMessage {
        private final HttpHeaders headers;

        private final InputStream body;

        public DecryptHttpInputMessage(HttpHeaders headers, InputStream body) {
            this.headers = headers;
            this.body = body;
        }

        @Override
        public InputStream getBody() {
            return body;
        }

        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }
    }
}

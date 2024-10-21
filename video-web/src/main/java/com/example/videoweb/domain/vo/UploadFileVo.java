package com.example.videoweb.domain.vo;

import com.example.videoweb.base.annotation.ReplaceIp;
import com.example.videoweb.base.annotation.ReplaceIpEntity;
import lombok.Data;

/**
 * @Author: chailei
 * @Date: 2024/10/21 16:28
 */
@Data
@ReplaceIpEntity
public class UploadFileVo {

    private String fileUrl;
    @ReplaceIp
    private String replaceFileUrl;

}

package com.example.videoweb.service.impl;

import com.example.videoweb.domain.entity.FileInfo;
import com.example.videoweb.mapper.FileInfoMapper;
import com.example.videoweb.service.IFileInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 文件上传表 服务实现类
 * </p>
 *
 * @author anubis
 * @since 2024-10-21
 */
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements IFileInfoService {

}

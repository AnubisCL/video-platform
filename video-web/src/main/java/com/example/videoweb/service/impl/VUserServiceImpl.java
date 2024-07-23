package com.example.videoweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.videoweb.dao.VUserDao;
import com.example.videoweb.domain.entity.VUser;
import com.example.videoweb.service.VUserService;
import org.springframework.stereotype.Service;

/**
 * 用户表(VUser)表服务实现类
 *
 * @author chailei
 * @since 2024-07-23 17:29:17
 */
@Service("vUserService")
public class VUserServiceImpl extends ServiceImpl<VUserDao, VUser> implements VUserService {

}


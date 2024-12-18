package com.example.videoweb.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.example.videoweb.base.annotation.ApiDecrypt;
import com.example.videoweb.base.annotation.FrequencyControl;
import com.example.videoweb.base.config.CacheConfig;
import com.example.videoweb.domain.cache.RSAInfo;
import com.example.videoweb.domain.dto.UserDto;
import com.example.videoweb.domain.entity.Menu;
import com.example.videoweb.domain.entity.Role;
import com.example.videoweb.domain.entity.User;
import com.example.videoweb.domain.enums.RoleEnum;
import com.example.videoweb.domain.enums.SignEnum;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.domain.vo.MenuVo;
import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.domain.vo.UserVo;
import com.example.videoweb.service.IMenuService;
import com.example.videoweb.service.IRoleService;
import com.example.videoweb.service.IUserService;
import com.example.videoweb.utils.RSAUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: anubis
 * @Date: 2024/7/23 15:19
 */
@Validated
@SaCheckLogin
@RestController
@RequestMapping("/authentication/")
public class AuthenticationController {

    @Resource private IUserService userService;
    @Resource private IRoleService roleService;
    @Resource private IMenuService menuService;

    @Value("${sign.enable}") private boolean signEnable;
    @Resource @Qualifier("ehCacheManager") private CacheManager ehCacheManager;
    private static Cache<Long, RSAInfo> rsaInfoCache;

    @PostConstruct
    public void init() {
        rsaInfoCache = ehCacheManager.getCache(CacheConfig.RSA_CACHE_NAME, Long.class, RSAInfo.class);
    }

    /**
     * 注册
     * @return
     */
    @SaIgnore
    @ApiDecrypt
    @PostMapping("signIn")
    public ResultVo signIn(@RequestBody @Valid UserDto userDto) {
        String signType = userDto.getSignType();
        if (signType.equals(SignEnum.SIGN_IN.getType())) {
            try {
                if (!signEnable) {
                    throw new RuntimeException("未开启注册");
                }
                userService.save(User.builder()
                        .email(userDto.getEmail().trim())
                        .passwordHash(userDto.getPasswordHash().trim())
                        .username(userDto.getUsername().trim())
                        .roleId(RoleEnum.NORMAL_CUSTOMER.getCode()).build());
            } catch (Exception e) {
                //用户名重复
                userDto.setSignType(SignEnum.LONG_IN.getType());
                return doLogin(userDto);
            }
            User one = userService.lambdaQuery().eq(User::getEmail, userDto.getEmail()).one();
            StpUtil.login(one.getUserId());
            setUserRSAInfoCache(one.getUserId());
            return ResultVo.data(StpUtil.getTokenValue());
        }
        return ResultVo.error("非法注册");
    }

    /**
     * 登录
     * @return
     */
    @SaIgnore
    @PostMapping("doLogin")
    public ResultVo doLogin(@RequestBody @Valid UserDto userDto) {
        String signType = userDto.getSignType();
        if (signType.equals(SignEnum.LONG_IN.getType())) {
            List<User> list = userService.lambdaQuery()
                    .eq(User::getUsername, userDto.getUsername().trim())
                    .or()
                    .eq(User::getEmail, userDto.getEmail().trim())
                    .list();
            if (!list.isEmpty()) {
                Optional<User> optionalUser = list.stream().filter(vUser -> vUser.getStatus().equals(StatusEnum.YES.getStatus()))
                        .filter(vUser -> vUser.getPasswordHash().equals(userDto.getPasswordHash().trim()))
                        .findFirst();
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    StpUtil.login(user.getUserId());
                    setUserRSAInfoCache(user.getUserId());
                    return ResultVo.data(StpUtil.getTokenValue());
                } else {
                    return ResultVo.error("用户名或密码错误");
                }
            }
            return ResultVo.error("用户不存在");
        }
        return ResultVo.error("登录失败");
    }

    /**
     * 判断是否登录
     * @return
     */
    //@FrequencyControl(time = 1, unit = TimeUnit.SECONDS, count = 3, target = FrequencyControl.Target.USERID)   //每秒3次
    @GetMapping("isLogin")
    public ResultVo isLogin() {
        return ResultVo.data(StpUtil.isLogin());
    }

    /**
     * 登出
     * @return
     */
    @GetMapping("signOut")
    public ResultVo signOut() {
        Long userId = StpUtil.getLoginIdAsLong();
        rsaInfoCache.remove(userId);
        StpUtil.logout();
        return ResultVo.data("登出成功");
    }

    @GetMapping("getUserInfo")
    public ResultVo getUserInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        User one = userService.lambdaQuery().eq(User::getUserId, userId).one();
        Long roleId = one.getRoleId();
        UserVo userVo = UserVo.builder()
                .userId(one.getUserId())
                .username(one.getUsername())
                .status(one.getStatus())
                .email(one.getEmail())
                .roleId(roleId).build();
        CompletableFuture.allOf(
            CompletableFuture.runAsync(() -> {
                userVo.setRoleName(roleService.lambdaQuery().eq(Role::getRoleId, roleId)
                        .eq(Role::getStatus, StatusEnum.YES.getStatus()).one().getRoleName());
            }),
            CompletableFuture.runAsync(() -> {
                userVo.setPermissions(roleService.getRolePermissionsByRoleId(roleId).keySet());
            }),
            CompletableFuture.runAsync(() -> {
                userVo.setPublicKey(rsaInfoCache.get(userId).getPublicKey());
            })
        ).join();
        return ResultVo.data(userVo);
    }

    @GetMapping("getPermissionList")
    public ResultVo getPermissionList() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userService.lambdaQuery().eq(User::getUserId, userId).one();
        return ResultVo.data(roleService.getRolePermissionsByRoleId(user.getRoleId()).keySet());
    }

    @GetMapping("getMenuList/{type}")
    public ResultVo getMenuList(@PathVariable(value = "type") String type) {
        Long userId = StpUtil.getLoginIdAsLong();
        User one = userService.lambdaQuery().eq(User::getUserId, userId).one();
        Long roleId = one.getRoleId();
        List<Menu> menuByRoleId = menuService.getMenuByRoleId(roleId);
        List<MenuVo> menuVoList = menuByRoleId.stream()
                .filter(f -> f.getMenuType().equals(type))
                .map(v -> MenuVo.builder()
                        .menuPath(v.getMenuPath())
                        .menuIcon(v.getMenuIcon())
                        .menuTitle(v.getMenuTitle())
                        .build()
                ).collect(Collectors.toList());
        return ResultVo.data(menuVoList);
    }


    public void setUserRSAInfoCache(Long userId) {
        Map<String, Object> genKeyPair = RSAUtil.genKeyPair();
        rsaInfoCache.put(userId, RSAInfo.builder()
                .publicKey(RSAUtil.getPublicKey(genKeyPair)).privateKey(RSAUtil.getPrivateKey(genKeyPair)).build());
    }
}

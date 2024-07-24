package com.example.videoweb.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.example.videoweb.domain.dto.UserDto;
import com.example.videoweb.domain.entity.Menu;
import com.example.videoweb.domain.entity.User;
import com.example.videoweb.domain.enums.RoleEnum;
import com.example.videoweb.domain.enums.SignEnum;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.domain.vo.MenuVo;
import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.service.IMenuService;
import com.example.videoweb.service.IRoleService;
import com.example.videoweb.service.IUserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: chailei
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

    /**
     * 注册
     * @return
     */
    @SaIgnore
    @PostMapping("signIn")
    public ResultVo signIn(@RequestBody @Valid UserDto userDto) {
        String signType = userDto.getSignType();
        if (signType.equals(SignEnum.SIGN_IN.getType())) {
            userService.save(User.builder()
                    .email(userDto.getEmail())
                    .passwordHash(userDto.getPasswordHash())
                    .username(userDto.getEmail())
                    .roleId(RoleEnum.NORMAL_CUSTOMER.getCode()).build());
            User one = userService.lambdaQuery().eq(User::getEmail, userDto.getEmail()).one();
            StpUtil.login(one.getUserId());
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
                    .eq(User::getUsername, userDto.getUsername())
                    .or()
                    .eq(User::getEmail, userDto.getUsername())
                    .list();
            if (!list.isEmpty()) {
                list.stream().filter(vUser -> vUser.getStatus().equals(StatusEnum.YES.getStatus()))
                    .filter(vUser -> vUser.getPasswordHash().equals(userDto.getPasswordHash()))
                    .findFirst().ifPresent(vUser -> StpUtil.login(vUser.getUserId()));
                return ResultVo.data(StpUtil.getTokenValue());
            }
            return ResultVo.error("用户不存在");
        }
        return ResultVo.error("登录失败");
    }

    /**
     * 判断是否登录
     * @return
     */
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
        StpUtil.logout();
        return ResultVo.data("登出成功");
    }

    @GetMapping("getUserInfo")
    public ResultVo getUserInfo() {
        String userId = StpUtil.getTokenValue();
        User one = userService.lambdaQuery().eq(User::getUserId, userId).one();
        return ResultVo.data(one);
    }

    @GetMapping("getPermissionList")
    public ResultVo getPermissionList() {
        String userId = StpUtil.getTokenValue();
        User one = userService.lambdaQuery().eq(User::getUserId, userId).one();
        Long roleId = one.getRoleId();
        return ResultVo.data(roleService.getRolePermissionsByRoleId(roleId).keySet());
    }

    @GetMapping("getMenuList")
    public ResultVo getMenuList() {
        String userId = StpUtil.getTokenValue();
        User one = userService.lambdaQuery().eq(User::getUserId, userId).one();
        Long roleId = one.getRoleId();
        List<Menu> menuByRoleId = menuService.getMenuByRoleId(roleId);
        List<MenuVo> menuVoList = menuByRoleId.stream()
                .map(v -> MenuVo.builder()
                        .path(v.getMenuPath())
                        .name(v.getMenuName())
                        .metaTitle(v.getMetaTitle())
                        .metaRequireAuth(v.getMetaRequireAuth())
                        .metaKeepAlive(v.getMetaKeepAlive())
                        .component(v.getMenuComponent()).build()
                ).collect(Collectors.toList());
        return ResultVo.data(menuVoList);
    }



}
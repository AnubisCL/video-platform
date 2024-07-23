package com.example.videoweb.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.example.videoweb.domain.dto.UserDto;
import com.example.videoweb.domain.entity.VMenu;
import com.example.videoweb.domain.entity.VUser;
import com.example.videoweb.domain.enums.RoleEnum;
import com.example.videoweb.domain.enums.SignEnum;
import com.example.videoweb.domain.enums.StatusEnum;
import com.example.videoweb.domain.vo.MenuVo;
import com.example.videoweb.domain.vo.ResultVo;
import com.example.videoweb.service.VMenuService;
import com.example.videoweb.service.VRoleService;
import com.example.videoweb.service.VUserService;
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

    @Resource private VUserService userService;
    @Resource private VRoleService roleService;
    @Resource private VMenuService menuService;

    /**
     * 注册
     * @return
     */
    @SaIgnore
    @PostMapping("signIn")
    public ResultVo signIn(@RequestBody @Valid UserDto userDto) {
        String signType = userDto.getSignType();
        if (signType.equals(SignEnum.SIGN_IN.getType())) {
            userService.save(VUser.builder()
                    .email(userDto.getEmail())
                    .passwordHash(userDto.getPasswordHash())
                    .username(userDto.getEmail())
                    .roleId(RoleEnum.NORMAL_CUSTOMER.getCode()).build());
            VUser one = userService.lambdaQuery().eq(VUser::getEmail, userDto.getEmail()).one();
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
            List<VUser> list = userService.lambdaQuery()
                    .eq(VUser::getUsername, userDto.getUsername())
                    .or()
                    .eq(VUser::getEmail, userDto.getUsername())
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
        VUser one = userService.lambdaQuery().eq(VUser::getUserId, userId).one();
        return ResultVo.data(one);
    }

    @GetMapping("getPermissionList")
    public ResultVo getPermissionList() {
        String userId = StpUtil.getTokenValue();
        VUser one = userService.lambdaQuery().eq(VUser::getUserId, userId).one();
        Long roleId = one.getRoleId();
        return ResultVo.data(roleService.getRolePermissionsByRoleId(roleId).keySet());
    }

    @GetMapping("getMenuList")
    public ResultVo getMenuList() {
        String userId = StpUtil.getTokenValue();
        VUser one = userService.lambdaQuery().eq(VUser::getUserId, userId).one();
        Long roleId = one.getRoleId();
        List<VMenu> menuByRoleId = menuService.getMenuByRoleId(roleId);
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

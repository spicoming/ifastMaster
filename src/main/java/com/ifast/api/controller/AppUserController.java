package com.ifast.api.controller;

import com.ifast.api.pojo.dto.UserLoginDTO;
import com.ifast.api.pojo.dto.UserLogoutDTO;
import com.ifast.api.pojo.vo.TokenVO;
import com.ifast.api.service.AppUserService;
import com.ifast.common.annotation.Log;
import com.ifast.common.controller.DictController;
import com.ifast.common.utils.Result;
import com.ifast.common.utils.ValidUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * <pre>
 *  基于jwt实现的API测试类
 * </pre>
 * 
 * <small> 2018年4月27日 | Aron</small>
 */
@RestController
@RequestMapping("/api/user/")
public class AppUserController {
    @Autowired
    private AppUserService userService;

    @PostMapping("login")
    @Log("api测试-登录")
    @ApiOperation("api测试-登录")
    public Result<?> token(@Valid @RequestBody final UserLoginDTO loginDTO, BindingResult result) {
        if(result.hasErrors()){
            //如果没有通过,跳转提示
            Map<String, String> map = ValidUtil.getErrors(result);
            return Result.failMap(map);
        }else{
            //继续业务逻辑
            TokenVO token = userService.getToken(loginDTO.getUname(), loginDTO.getPasswd());
            return Result.ok(token);
        }
    }
    
    @PostMapping("refresh")
    @Log("api测试-刷新token")
    @ApiOperation("api测试-刷新token")
    public Result<?> refresh(@RequestParam String uname, @RequestBody final String refreshToken) {
    	TokenVO token = userService.refreshToken(uname, refreshToken);
    	return Result.ok(token);
    }
    
    @PostMapping("logout")
    @Log("api测试-注销token")
    @ApiOperation("api测试-注销token")
    public Result<?> logout(@RequestBody UserLogoutDTO dto) {
    	userService.logoutToken(dto.getToken(), dto.getRefreshToken());
    	return Result.ok();
    }

    @GetMapping("/require_auth")
    @RequiresAuthentication
    @Log("api测试-需要认证才能访问")
    @ApiOperation("api测试-需要认证才能访问")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", value = "Authorization", paramType = "header") })
    public Result<?> requireAuth() {
        return Result.build(200, "认证通过", null);
    }

    @GetMapping("/require_role")
    @RequiresRoles("apiRole")
    @Log("api测试-需要api角色才能访问")
    @ApiOperation("api测试-需要api角色才能访问")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", value = "Authorization", paramType = "header") })
    public Result<?> requireRole() {
        return Result.build(200, "用户有role角色权限", null);
    }

    @GetMapping("/require_permission")
    @RequiresPermissions("api:user:update")
    @Log("api测试-需要api:user:update权限才能访问")
    @ApiOperation("api测试-需要api:user:update权限才能访问")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", value = "Authorization", paramType = "header") })
    public Result<?> requirePermission() {
        return Result.build(200, "用户有api:user:update权限", null);
    }

}

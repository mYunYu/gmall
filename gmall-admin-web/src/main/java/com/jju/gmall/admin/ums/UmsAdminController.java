package com.jju.gmall.admin.ums;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jju.gmall.admin.ums.vo.UmsAdminLoginParam;
import com.jju.gmall.admin.ums.vo.UmsAdminParam;
import com.jju.gmall.admin.utils.JwtTokenUtil;
import com.jju.gmall.ums.entity.Admin;
import com.jju.gmall.ums.service.AdminService;
import com.jju.gmall.to.CommonResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台用户管理
 * Created by atguigu 4/26.
 */
@Slf4j
@CrossOrigin
@Controller
@Api(tags = "AdminController", description = "后台用户管理")
@RequestMapping("/admin")
public class UmsAdminController {
    @Reference
    private AdminService adminService;
    @Value("${gmall.jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${gmall.jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    /**
     *  注册成功返回用户的所有信息
     * @param umsAdminParam
     * @param result
     * @return
     */
    @ApiOperation(value = "用户注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Object register(@Valid @RequestBody UmsAdminParam umsAdminParam, BindingResult result) {
        //校验参数   直接在aop中处理
        log.debug("需要注册的用户详情：{}", umsAdminParam);

        Admin admin = null;
        //TODO 完成注册功能

        return new CommonResult().success(admin);
    }

    /**
     *  登录成功以后，用户的信息以jwt令牌的方式返回
     * @param umsAdminLoginParam
     * @param result
     * @return
     */
    @ApiOperation(value = "登录以后返回token")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Object login(@RequestBody UmsAdminLoginParam umsAdminLoginParam, BindingResult result) {
        //去数据库登陆
        Admin admin = adminService.login(umsAdminLoginParam.getUsername(), umsAdminLoginParam.getPassword());

        //登陆成功生成token，此token携带基本用户信息，以后就不用去数据库了
        String token = jwtTokenUtil.generateToken(admin);
        if (token == null) {
            return new CommonResult().validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return new CommonResult().success(tokenMap);
    }

    @ApiOperation(value = "刷新token")
    @RequestMapping(value = "/token/refresh", method = RequestMethod.GET)
    @ResponseBody
    public Object refreshToken(HttpServletRequest request) {
        //1、获取请求头中的Authorization完整值
        String oldToken = request.getHeader(tokenHeader);
        String refreshToken = "";

        //2、从请求头中的Authorization中分离出jwt的值
        String token = oldToken.substring(tokenHead.length());

        //3、是否可以进行刷新（未过刷新时间）
        if (jwtTokenUtil.canRefresh(token)) {
            refreshToken =  jwtTokenUtil.refreshToken(token);
        }else  if(refreshToken == null && "".equals(refreshToken)){
            return new CommonResult().failed();
        }

        //将新的token交给前端
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", refreshToken);
        tokenMap.put("tokenHead", tokenHead);
        return new CommonResult().success(tokenMap);
    }

    @ApiOperation(value = "获取当前登录用户信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public Object getAdminInfo(HttpServletRequest request) {
        String oldToken = request.getHeader(tokenHeader);
        String userName = jwtTokenUtil.getUserNameFromToken(oldToken.substring(tokenHead.length()));

        //1、getOne是mybatis-plus生成的，而且带了泛型
        //2、dubbo没办法直接调用mp中带放心的service
        //mp自动生成有可能有兼容问题，最好不要远程调用
//        Admin umsAdmin = adminService.getOne(new QueryWrapper<Admin>().eq("username",userName));
        Admin umsAdmin = adminService.getUserInfo(userName);

        Map<String, Object> data = new HashMap<>();
        data.put("username", umsAdmin.getUsername());
        data.put("roles", new String[]{"TEST"});
        data.put("icon", umsAdmin.getIcon());
        return new CommonResult().success(data);
    }

    @ApiOperation(value = "登出功能")
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public Object logout() {
        //TODO 用户退出

        return new CommonResult().success(null);
    }

    @ApiOperation("根据用户名或姓名分页获取用户列表")
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @ResponseBody
    public Object list(@RequestParam(value = "name",required = false) String name,
                       @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum){
        //TODO 分页查询用户信息

        //TODO 响应需要包含分页信息；详细查看swagger规定
        return new CommonResult().failed();
    }

    @ApiOperation("获取指定用户信息")
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    @ResponseBody
    public Object getItem(@PathVariable Long id){

        //TODO 获取指定用户信息
        return new CommonResult().failed();
    }

    @ApiOperation("更新指定用户信息")
    @RequestMapping(value = "/update/{id}",method = RequestMethod.POST)
    @ResponseBody
    public Object update(@PathVariable Long id,@RequestBody Admin admin){

        //TODO 更新指定用户信息
        return new CommonResult().failed();
    }

    @ApiOperation("删除指定用户信息")
    @RequestMapping(value = "/delete/{id}",method = RequestMethod.POST)
    @ResponseBody
    public Object delete(@PathVariable Long id){
        //TODO 删除指定用户信息
        return new CommonResult().failed();
    }

    @ApiOperation("给用户分配角色")
    @RequestMapping(value = "/role/update",method = RequestMethod.POST)
    @ResponseBody
    public Object updateRole(@RequestParam("adminId") Long adminId,
                             @RequestParam("roleIds") List<Long> roleIds){
        //TODO 给用户分配角色
        return new CommonResult().failed();
    }

    @ApiOperation("获取指定用户的角色")
    @RequestMapping(value = "/role/{adminId}",method = RequestMethod.GET)
    @ResponseBody
    public Object getRoleList(@PathVariable Long adminId){
        //TODO 获取指定用户的角色

        return new CommonResult().success(null);
    }

    @ApiOperation("给用户分配(增减)权限")
    @RequestMapping(value = "/permission/update",method = RequestMethod.POST)
    @ResponseBody
    public Object updatePermission(@RequestParam Long adminId,
                                   @RequestParam("permissionIds") List<Long> permissionIds){
        //TODO 给用户分配(增减)权限

        return new CommonResult().failed();
    }

    @ApiOperation("获取用户所有权限（包括+-权限）")
    @RequestMapping(value = "/permission/{adminId}",method = RequestMethod.GET)
    @ResponseBody
    public Object getPermissionList(@PathVariable Long adminId){
        //TODO 获取用户所有权限（包括+-权限）
        return new CommonResult().failed();
    }
}

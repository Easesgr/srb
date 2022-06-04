package com.anyi.srb.controller;


import com.alibaba.fastjson.JSON;
import com.anyi.srb.common.R;
import com.anyi.srb.hfb.RequestHelper;
import com.anyi.srb.service.UserBindService;
import com.anyi.srb.utils.JwtUtils;
import com.anyi.srb.entity.vo.UserBindVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 前端控制器
 * </p>
 *
 * @author anyi
 * @since 2022-05-31
 */
@RestController
@Api(tags = "账户绑定")
@Slf4j
@RequestMapping("/srb/front/userBind")
public class UserBindController {

    @Resource
    private UserBindService userBindService;

    @ApiOperation("账户绑定提交数据")
    @PostMapping("/auth/bind")
    public R bind(@RequestBody UserBindVO userBindVO , HttpServletRequest request){
        // 获取token，从里面拿到userId
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        // 将usrInfo 和 userBind绑定
        String formString = userBindService.formString(userBindVO,userId);
        return R.ok().data("formString",formString);
    }

    @ApiOperation("返回接口调用")
    @PostMapping("/notify")
    public String  notify(HttpServletRequest request){
        // 获取返回的所有参数，封装成map
        Map<String, Object> map = RequestHelper.switchMap(request.getParameterMap());

        // 对签名进行校验
        //校验签名
        if(!RequestHelper.isSignEquals(map)) {
            log.error("用户账号绑定异步回调签名错误：" + JSON.toJSONString(map));
            return "fail";
        }
        //修改绑定状态
        userBindService.notify(map);
        return "success";
    }
}


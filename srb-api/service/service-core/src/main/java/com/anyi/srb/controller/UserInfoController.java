package com.anyi.srb.controller;


import cn.hutool.core.lang.Validator;
import com.anyi.srb.common.R;
import com.anyi.srb.common.RedisField;
import com.anyi.srb.common.ResponseEnum;
import com.anyi.srb.entity.UserInfo;
import com.anyi.srb.entity.vo.UserIndexVO;
import com.anyi.srb.exception.BusinessException;
import com.anyi.srb.service.UserInfoService;
import com.anyi.srb.utils.JwtUtils;
import com.anyi.srb.entity.vo.LoginVO;
import com.anyi.srb.entity.vo.RegisterVO;
import com.anyi.srb.entity.vo.UserInfoVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author anyi
 * @since 2022-05-31
 */
@RestController
//@CrossOrigin
@Api(tags = "用户管理")
@RequestMapping("/srb/front/user/info")
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private RedisTemplate redisTemplate;
    @PostMapping("/register")
    @ApiOperation("注册")
    public R login(@RequestBody RegisterVO registerVO){
        // 对数据进行校验
        if (registerVO.getMobile() == null){
            throw new BusinessException("请输入手机号");
        }
        boolean isPhone = Validator.isMobile(registerVO.getMobile());
        if (!isPhone){
            throw new BusinessException("手机号格式错误");
        }

        String  code = (String) redisTemplate.
                opsForValue().get(RedisField.PHONE_CODE + registerVO.getMobile());
        if (code == null || !code.equals(registerVO.getCode())){
            throw new BusinessException("验证码错误!");
        }

        userInfoService.register(registerVO);
        return R.ok().message("注册成功！");
    }

    @ApiOperation("登录")
    @PostMapping("/login")
    public R login(@RequestBody LoginVO loginVO, HttpServletRequest request){

        if (loginVO.getMobile() == null || loginVO.getPassword() == null){
            throw new BusinessException("密码不能为空");
        }
        String ip = request.getRemoteAddr();
        UserInfoVO userInfoVO = userInfoService.login(loginVO,ip);

        return R.ok().data("userInfo",userInfoVO);
    }

    @ApiOperation("校验令牌")
    @GetMapping("/checkToken/{mobile}")
    public R checkToken(@PathVariable String mobile) {

        String  token = (String) redisTemplate.opsForValue().get(RedisField.USER_TOKEN + mobile);
        boolean result = JwtUtils.checkToken(token);
        if(result){
            return R.ok();
        }else{
            //LOGIN_AUTH_ERROR(-211, "未登录"),
            return R.setResult(ResponseEnum.LOGIN_AUTH_ERROR);
        }
    }
    @GetMapping("/checkMoblie/{mobile}")
    public boolean checkMobile(@PathVariable String mobile){
        UserInfo one = userInfoService.getOne(new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getMobile, mobile));
        return one != null;
    }
    @ApiOperation("获取个人空间用户信息")
    @GetMapping("/getIndexUserInfo")
    public R getIndexUserInfo(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        UserIndexVO userIndexVO = userInfoService.getIndexUserInfo(userId);
        return R.ok().data("userIndexVO", userIndexVO);
    }

}


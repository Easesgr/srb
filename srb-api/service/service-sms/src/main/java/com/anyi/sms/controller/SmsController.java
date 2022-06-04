package com.anyi.sms.controller;

import com.anyi.sms.service.SmsService;
import com.anyi.srb.common.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 安逸i
 * @version 1.0
 */
@RestController
@RequestMapping("/api/sms")
@Api(tags = "短信管理")
//@CrossOrigin //跨域
@Slf4j
public class SmsController {

    @Resource
    private SmsService smsService;


    @ApiOperation("发送短信")
    @GetMapping("/send/{phone}")
    public R sendCode(@PathVariable String phone){
        smsService.sendCode(phone);
        return R.ok().message("发送验证码成功");
    }
}

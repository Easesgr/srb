package com.anyi.srb.controller;


import com.alibaba.fastjson.JSON;
import com.anyi.srb.common.R;
import com.anyi.srb.entity.LendReturn;
import com.anyi.srb.hfb.RequestHelper;
import com.anyi.srb.service.LendReturnService;
import com.anyi.srb.utils.JwtUtils;
import com.netflix.ribbon.proxy.annotation.Http;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 还款记录表 前端控制器
 * </p>
 *
 * @author anyi
 * @since 2022-06-02
 */
@RestController
@RequestMapping("/srb/front/lendReturn")
@Slf4j
public class LendReturnController {
    @Resource
    private LendReturnService lendReturnService;

    @ApiOperation("获取列表")
    @GetMapping("/list/{lendId}")
    public R list(
            @ApiParam(value = "标的id", required = true)
            @PathVariable Long lendId , HttpServletRequest request) {
        Long userId = JwtUtils.getUserId(request.getHeader("token"));
        List<LendReturn> list = lendReturnService.selectByLendId(lendId,userId);
        return R.ok().data("list", list);
    }
    @ApiOperation("用户还款")
    @PostMapping("/commitReturn/{lendReturnId}")
    public R commitReturn(
            @ApiParam(value = "还款计划id", required = true)
            @PathVariable Long lendReturnId, HttpServletRequest request) {

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        String formStr = lendReturnService.commitReturn(lendReturnId, userId);
        return R.ok().data("formStr", formStr);
    }
    @ApiOperation("还款异步回调")
    @PostMapping("/notifyUrl")
    public String notifyUrl(HttpServletRequest request) {

        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());


        //校验签名
        if(RequestHelper.isSignEquals(paramMap)) {
            if("0001".equals(paramMap.get("resultCode"))) {
                lendReturnService.notify(paramMap);
            } else {
                log.info("还款异步回调失败：" + JSON.toJSONString(paramMap));
                return "fail";
            }
        } else {
            log.info("还款异步回调签名错误：" + JSON.toJSONString(paramMap));
            return "fail";
        }
        return "success";
    }
}


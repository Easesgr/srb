package com.anyi.srb.controller;


import com.anyi.srb.common.R;
import com.anyi.srb.common.ResponseEnum;
import com.anyi.srb.entity.BorrowInfo;
import com.anyi.srb.exception.BusinessException;
import com.anyi.srb.service.BorrowInfoService;
import com.anyi.srb.service.BorrowerService;
import com.anyi.srb.utils.JwtUtils;
import io.jsonwebtoken.Jwt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * <p>
 * 借款信息表 前端控制器
 * </p>
 *
 * @author anyi
 * @since 2022-06-02
 */
@Api(tags = "借款信息管理")
@RestController
@RequestMapping("/srb/front/borrowerInfo")
public class BorrowInfoController {

    @Resource
    private BorrowInfoService borrowInfoService;

    @ApiOperation("获取借款额度")
    @GetMapping("/getMoney")
    public R getMoney(HttpServletRequest request){
        // 获取到用户id
        String token = request.getHeader("token");
        boolean b = JwtUtils.checkToken(token);
        if (!b){
            throw new BusinessException(ResponseEnum.LOGIN_AUTH_ERROR);
        }
        Long userId = JwtUtils.getUserId(token);
        BigDecimal money = borrowInfoService.getMoney(userId);
        return R.ok().data("money",money);
    }

    @ApiOperation("提交借款申请")
    @PostMapping("/save")
    public R save(@RequestBody BorrowInfo borrowInfo, HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        borrowInfoService.saveBorrowerInfo(borrowInfo,userId);
        return R.ok().message("提交成功！");
    }
    @ApiOperation("获取当前审核状态")
    @GetMapping("/checkStatus")
    public R getStatus(HttpServletRequest request){
        String token = request.getHeader("token");
        boolean b = JwtUtils.checkToken(token);
        if (!b){
            throw new BusinessException(ResponseEnum.LOGIN_AUTH_ERROR);
        }
        Integer status = borrowInfoService.getStatus(JwtUtils.getUserId(token));
        return R.ok().data("status",status);
    }
}


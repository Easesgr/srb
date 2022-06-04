package com.anyi.srb.controller;


import com.anyi.srb.common.R;
import com.anyi.srb.common.ResponseEnum;
import com.anyi.srb.entity.Lend;
import com.anyi.srb.entity.UserAccount;
import com.anyi.srb.exception.BusinessException;
import com.anyi.srb.service.LendService;
import com.anyi.srb.service.UserAccountService;
import com.anyi.srb.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 前端控制器
 * </p>
 *
 * @author anyi
 * @since 2022-06-02
 */
@Api(tags = "标的")
@RestController
@RequestMapping("/srb/front/lend")
@Slf4j
public class LendController {

    @Resource
    private LendService lendService;

    @Resource
    private UserAccountService userAccountService;


    @ApiOperation("获取标的列表")
    @GetMapping("/list")
    public R list() {
        List<Lend> lendList = lendService.selectList();
        return R.ok().data("lendList", lendList);
    }
    @ApiOperation("获取标的详细信息")
    @GetMapping("show/{id}")
    public R getById(@PathVariable Long id , HttpServletRequest request){
        Map<String, Object> lendDetail = lendService.getLendDetail(id);
        return R.ok().data("lendDetail", lendDetail);
    }

    @ApiOperation("获取账户余额")
    @GetMapping("/getCount")
    public R getCount(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        if (token == null){
            throw new BusinessException(ResponseEnum.LOGIN_AUTH_ERROR);
        }
        BigDecimal account = userAccountService.getCount(userId);
        return R.ok().data("account",account);
    }
    @ApiOperation("计算收益")
    @GetMapping("/getInterestCount/{invest}/{yearRate}/{totalMonth}/{returnMethod}")
    public R getInterestCount(
            @ApiParam(value = "投资金额", required = true)
            @PathVariable("invest") BigDecimal invest,

            @ApiParam(value = "年化收益", required = true)
            @PathVariable("yearRate")BigDecimal yearRate,

            @ApiParam(value = "期数", required = true)
            @PathVariable("totalMonth")Integer totalMonth,

            @ApiParam(value = "还款方式", required = true)
            @PathVariable("returnMethod")Integer returnMethod) {

        BigDecimal  interestCount = lendService.getInterestCount(invest, yearRate, totalMonth, returnMethod);
        return R.ok().data("interestCount", interestCount);
    }
}


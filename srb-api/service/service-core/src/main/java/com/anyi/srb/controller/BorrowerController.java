package com.anyi.srb.controller;


import com.anyi.srb.common.R;
import com.anyi.srb.common.ResponseEnum;
import com.anyi.srb.exception.BusinessException;
import com.anyi.srb.service.BorrowerService;
import com.anyi.srb.utils.JwtUtils;
import com.anyi.srb.entity.vo.BorrowerVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 借款人 前端控制器
 * </p>
 *
 * @author anyi
 * @since 2022-06-02
 */
@RestController
@RequestMapping("/srb/front/borrower")
public class BorrowerController {
    @Resource
    private BorrowerService borrowerService;

    @PostMapping("/save")
    public R saveBorrower(@RequestBody BorrowerVO borrowerVO, HttpServletRequest request){
        // 身份验证
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        boolean b = JwtUtils.checkToken(token);
        if (!b){
            throw new BusinessException(ResponseEnum.LOGIN_AUTH_ERROR);
        }
        borrowerService.saveBorrower(borrowerVO,userId);
        return R.ok().message("保存成功！");
    }

    @GetMapping("/checkStatus")
    public R checkStatus(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        Integer status = borrowerService.checkStatus(userId);
        return R.ok().data("status",status);
    }
}


package com.anyi.srb.controller.admin;

import com.anyi.srb.common.R;
import com.anyi.srb.entity.UserLoginRecord;
import com.anyi.srb.service.UserLoginRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 安逸i
 * @version 1.0
 */
@Api(tags = "会员登录日志接口")
@RestController
@RequestMapping("/srb/admin/userLoginRecord")
@Slf4j
//@CrossOrigin
public class AdminUserLoginRecordController {

    @Resource
    private UserLoginRecordService userLoginRecordService;

    @GetMapping("/list/{userId}")
    @ApiOperation("查询登入日志")
    public R list(@PathVariable Long userId){
        // 根据id查询出分页信息
        Page<UserLoginRecord> page = new Page<>(1,10);
        LambdaQueryWrapper<UserLoginRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLoginRecord::getUserId,userId);
        userLoginRecordService.page(page,wrapper);
        return R.ok().data("items",page.getRecords());
    }
}

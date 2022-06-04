package com.anyi.srb.controller.admin;

import com.anyi.srb.common.R;
import com.anyi.srb.entity.UserInfo;
import com.anyi.srb.entity.query.UserInfoQuery;
import com.anyi.srb.service.UserInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 安逸i
 * @version 1.0
 */

@Api(tags = "会员管理")
@RestController
@RequestMapping("srb/admin/userInfo")
@Slf4j
//@CrossOrigin
public class AdminUserInfoController {
    @Resource
    private UserInfoService userInfoService;

    @ApiOperation("获取会员分页列表")
    @PostMapping("/list/{page}/{limit}")
    public R listPage(
            @ApiParam(value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(value = "每页记录数", required = true)
            @PathVariable Long limit,

            @RequestBody(required = false)
                    UserInfoQuery userInfoQuery) {

        Page<UserInfo> pageParam = new Page<>(page, limit);
        IPage<UserInfo> pageModel = userInfoService.listPage(pageParam, userInfoQuery);
        return R.ok().data("pageModel", pageModel);
    }
    @ApiOperation("修改账户状态")
    @GetMapping("/change/{userId}/{status}")
    public R change(@PathVariable Long userId,@PathVariable Integer status){
        UserInfo userInfo = new UserInfo();
        userInfo.setId(userId);
        userInfo.setStatus(status);
        userInfoService.updateById(userInfo);
        return R.ok();
    }
}

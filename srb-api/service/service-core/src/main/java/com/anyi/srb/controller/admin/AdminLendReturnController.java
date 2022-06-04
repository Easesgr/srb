package com.anyi.srb.controller.admin;

import com.anyi.srb.common.R;
import com.anyi.srb.entity.LendItemReturn;
import com.anyi.srb.entity.LendReturn;
import com.anyi.srb.service.LendReturnService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 安逸i
 * @version 1.0
 */

@Api(tags = "还款记录")
@RestController
@RequestMapping("srb/admin/lendReturn")
@Slf4j
public class AdminLendReturnController {

    @Resource
    private LendReturnService lendReturnService;

    @ApiOperation("获取列表")
    @GetMapping("/list/{lendId}")
    public R list(
            @ApiParam(value = "标的id", required = true)
            @PathVariable Long lendId) {
        List<LendReturn> list = lendReturnService.list(new LambdaQueryWrapper<LendReturn>().eq(LendReturn::getLendId, lendId));
        return R.ok().data("list", list);
    }
}

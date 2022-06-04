package com.anyi.srb.controller.admin;

import com.anyi.srb.common.R;
import com.anyi.srb.entity.Borrower;
import com.anyi.srb.service.BorrowerService;
import com.anyi.srb.entity.vo.BorrowerApprovalVO;
import com.anyi.srb.entity.vo.BorrowerDetailVO;
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
@Api(tags = "借款人管理")
@RestController
@RequestMapping("/srb/admin/borrower")
@Slf4j
public class AdminBorrowerController {

    @Resource
    private BorrowerService borrowerService;

    @ApiOperation("获取借款人分页列表")
    @GetMapping("/list/{page}/{limit}")
    public R listPage(
            @ApiParam(value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(value = "每页记录数", required = true)
            @PathVariable Long limit,

            @ApiParam(value = "查询关键字", required = false)
            @RequestParam String keyword) {
        //这里的@RequestParam其实是可以省略的，但是在目前的swagger版本中（2.9.2）不能省略，
        //否则默认将没有注解的参数解析为body中的传递的数据

        Page<Borrower> pageParam = new Page<>(page, limit);
        IPage<Borrower> pageModel = borrowerService.listPage(pageParam, keyword);
        return R.ok().data("pageModel", pageModel);
    }

    @GetMapping("/show/{id}")
    public R show(@PathVariable Long id){
        BorrowerDetailVO borrowerDetailVO = borrowerService.show(id);
        return R.ok().data("borrowerInfo",borrowerDetailVO);
    }
    @PostMapping("/approval")
    public R approval(@RequestBody BorrowerApprovalVO borrowerApprovalVO){

        borrowerService.approval(borrowerApprovalVO);
        return R.ok().message("审批成功！");
    }
}
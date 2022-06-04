package com.anyi.srb.controller.admin;

import com.anyi.srb.common.R;
import com.anyi.srb.entity.BorrowInfo;
import com.anyi.srb.service.BorrowInfoService;
import com.anyi.srb.entity.vo.BorrowInfoApprovalVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author 安逸i
 * @version 1.0
 */
@Api(tags = "借款后台管理")
@RestController
@RequestMapping("srb/admin/borrowInfo")
@Slf4j
public class AdminBorrowInfoController {

    @Resource
    private BorrowInfoService borrowInfoService;

    @ApiOperation("借款信息列表")
    @GetMapping("/list")
    public R list() {
        List<BorrowInfo> borrowInfoList = borrowInfoService.selectList();
        return R.ok().data("list", borrowInfoList);
    }

    @ApiOperation("获取借款信息")
    @GetMapping("/show/{id}")
    public R show(
            @ApiParam(value = "借款id", required = true)
            @PathVariable Long id) {
        Map<String, Object> borrowInfoDetail = borrowInfoService.getBorrowInfoDetail(id);
        return R.ok().data("borrowInfoDetail", borrowInfoDetail);
    }

    @ApiOperation("审核借款信息")
    @PostMapping("/approval")
    public R approval(@RequestBody BorrowInfoApprovalVO approvalVO){
        borrowInfoService.approval(approvalVO);
        return R.ok().message("审核成功！");
    }
}

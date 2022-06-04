package com.anyi.srb.controller.admin;

import com.anyi.srb.common.R;
import com.anyi.srb.entity.IntegralGrade;
import com.anyi.srb.service.IntegralGradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 安逸i
 * @version 1.0
 */
//@CrossOrigin
@RestController
@RequestMapping("/srb/admin/integralGrade")
@Api(tags = "积分管理")
public class AdminIntegralGradeController {

    @Resource
    private IntegralGradeService integralGradeService;

    /**
     * 输出列表
     * @return
     */
    @ApiOperation("分页查询")
    @GetMapping("/list")
    public R list(){
        List<IntegralGrade> list = integralGradeService.list();
        return R.ok().data("items",list);
    }

    @ApiOperation("根据id删除积分")
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Integer id){
        boolean b = integralGradeService.removeById(id);
        return R.ok().message("删除成功");
    }

    @ApiOperation("根据id删除积分")
    @GetMapping("/{id}")
    public R getById(@PathVariable Integer id){
        IntegralGrade byId = integralGradeService.getById(id);
        return R.ok().data("item",byId);
    }

    @ApiOperation("保存积分信息")
    @PostMapping("/save")
    public R save(@RequestBody IntegralGrade integralGrade){
        boolean save = integralGradeService.save(integralGrade);
        if (save){
            return R.ok().message("保存成功");
        }else {
            return R.ok().message("保存失败");
        }
    }

    @ApiOperation("更新积分信息")
    @PutMapping("/update")
    public R update(@RequestBody IntegralGrade integralGrade){
        boolean save = integralGradeService.updateById(integralGrade);
        if (save){
            return R.ok().message("更新成功");
        }else {
            return R.ok().message("更新失败");
        }
    }
}

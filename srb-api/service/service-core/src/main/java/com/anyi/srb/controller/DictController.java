package com.anyi.srb.controller;

import com.anyi.srb.common.R;
import com.anyi.srb.common.ResponseEnum;
import com.anyi.srb.entity.Dict;
import com.anyi.srb.exception.BusinessException;
import com.anyi.srb.service.DictService;
import com.anyi.srb.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 安逸i
 * @version 1.0
 */
@RestController
@Api(tags = "字典管理")
@RequestMapping("/srb/front/dict")
public class DictController {
    @Resource
    private DictService dictService;

    @GetMapping("/{dictCode}")
    @ApiOperation("根据dictCode字典分类信息")
    public R getByDictCode(@PathVariable String dictCode){
        List<Dict> list = dictService.getByDictCode(dictCode);
        return R.ok().data("dictList",list);
    }
}

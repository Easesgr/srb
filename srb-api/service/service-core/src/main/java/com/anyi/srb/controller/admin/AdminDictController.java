package com.anyi.srb.controller.admin;


import com.alibaba.excel.EasyExcel;
import com.anyi.srb.common.R;
import com.anyi.srb.common.RedisField;
import com.anyi.srb.common.ResponseEnum;
import com.anyi.srb.entity.dto.ExcelDictDTO;
import com.anyi.srb.entity.Dict;
import com.anyi.srb.exception.BusinessException;
import com.anyi.srb.service.DictService;
import com.sun.deploy.net.URLEncoder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author anyi
 * @since 2022-05-31
 */
@RestController
@Api(tags = "字典管理")
@RequestMapping("/srb/admin/dict")
//@CrossOrigin
public class AdminDictController {
    @Resource
    private DictService dictService;
    @Resource
    private RedisTemplate redisTemplate;

    @ApiOperation("excel导入")
    @PostMapping("/import")
    public R importExcel(MultipartFile file){

        try {
            InputStream inputStream = file.getInputStream();
            dictService.importData(inputStream);
            return R.ok().message("批量导入成功");
        } catch (Exception e) {
            //UPLOAD_ERROR(-103, "文件上传错误"),
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR, e);
        }
    }

    @ApiOperation("导出excel")
    @GetMapping("export")
    public void export(HttpServletResponse response){
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("mydict", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), ExcelDictDTO.class).sheet("数据字典").doWrite(dictService.listDictData());
        } catch (IOException e) {
            throw  new BusinessException(ResponseEnum.EXPORT_DATA_ERROR, e);
        }
    }

    @ApiOperation("根据父节点id查询子节点信息")
    @GetMapping("/{parentId}")
    public R listByParentId(@PathVariable Long parentId){
        List<Dict> list = null;
        // 从redis中获取
        list = (List<Dict>) redisTemplate.opsForValue().get(RedisField.DICT + parentId.toString());
        if (list !=null && list.size()>0){
            return R.ok().data("list",list);
        }else {
            list = dictService.listByParentId(parentId);
            redisTemplate.opsForValue().set(RedisField.DICT + parentId.toString(), list,30, TimeUnit.MINUTES);
        }
        return R.ok().data("list",list);
    }
}


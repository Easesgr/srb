package com.anyi.serviceoss.controller;


import com.anyi.serviceoss.serivcie.OssService;
import com.anyi.srb.common.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author 安逸i
 * @version 1.0
 */
@RestController
@RequestMapping("/srb/oss")
//@CrossOrigin
public class OssController {
    @Resource
    private OssService ossService;

    @PostMapping("/uploadFile")
    public R uploadFile(MultipartFile file,String module){
        String url = ossService.uploadFile(file,module);
        return R.ok().data("url",url);
    }

    /**
     * 删除文件远程方法
     */
    @PostMapping(value = "/deleteFile")
    @ApiOperation(value = "单个删除", notes = "单个删除")
    public R deleteFile(String fileName) {
        //获取删除文件  fileName
        /**
         * 填写文件名。文件名包含路径，不包含Bucket名称。
         * 例如2021/09/14/52c6a3114e634979a2934f1ea12deaadfile.png。
         */
        boolean flag =  ossService.deleteFile(fileName);
        if (flag){
            return R.ok().message("删除成功");
        }  else {
            return R.ok().message("删除失败");
        }
    }
}

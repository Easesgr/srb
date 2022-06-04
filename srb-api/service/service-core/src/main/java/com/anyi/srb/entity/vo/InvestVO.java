package com.anyi.srb.entity.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author 安逸i
 * @version 1.0
 */
@Data
@ApiModel(description = "投标信息")
public class InvestVO {

    private Long lendId;

    //投标金额
    private String investAmount;

    //用户id
    private Long investUserId;

    //用户姓名
    private String investName;
}
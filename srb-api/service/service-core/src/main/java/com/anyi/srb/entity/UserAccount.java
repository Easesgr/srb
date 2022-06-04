package com.anyi.srb.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户账户
 * </p>
 *
 * @author anyi
 * @since 2022-05-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="UserAccount对象", description="用户账户")
public class UserAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "帐户可用余额")
    private BigDecimal amount;

    @ApiModelProperty(value = "冻结金额")
    private BigDecimal freezeAmount;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @TableLogic
    @ApiModelProperty(value = "逻辑删除(1:已删除，0:未删除)")
    private Boolean isDeleted;

    @ApiModelProperty(value = "版本号")
    private Integer version;


}

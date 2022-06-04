package com.anyi.srb.service;

import com.anyi.srb.entity.BorrowerAttach;
import com.anyi.srb.entity.vo.BorrowerAttachVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务类
 * </p>
 *
 * @author anyi
 * @since 2022-06-02
 */
public interface BorrowerAttachService extends IService<BorrowerAttach> {

    List<BorrowerAttachVO> getAttachVo(Long borrowerId);
}

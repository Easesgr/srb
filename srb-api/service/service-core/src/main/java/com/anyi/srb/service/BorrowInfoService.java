package com.anyi.srb.service;

import com.anyi.srb.entity.BorrowInfo;
import com.anyi.srb.entity.vo.BorrowInfoApprovalVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务类
 * </p>
 *
 * @author anyi
 * @since 2022-06-02
 */
public interface BorrowInfoService extends IService<BorrowInfo> {

    BigDecimal getMoney(Long userId);

    void saveBorrowerInfo(BorrowInfo borrowInfo, Long userId);

    Integer getStatus(Long userId);

    List<BorrowInfo> selectList();

    Map<String, Object> getBorrowInfoDetail(Long id);

    void approval(BorrowInfoApprovalVO approvalVO);
}

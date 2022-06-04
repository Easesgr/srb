package com.anyi.srb.service;

import com.anyi.srb.entity.BorrowInfo;
import com.anyi.srb.entity.Lend;
import com.anyi.srb.entity.vo.BorrowInfoApprovalVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 服务类
 * </p>
 *
 * @author anyi
 * @since 2022-06-02
 */
public interface LendService extends IService<Lend> {

    void createLend(BorrowInfoApprovalVO approvalVO, BorrowInfo borrowInfo);

    List<Lend> selectList();

    Map<String, Object> getLendDetail(Long id);

    BigDecimal getInterestCount(BigDecimal invest, BigDecimal yearRate, Integer totalMonth, Integer returnMethod);

    void makeLoan(Long id);
}

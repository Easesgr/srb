package com.anyi.srb.service;

import com.anyi.srb.entity.Borrower;
import com.anyi.srb.entity.vo.BorrowerApprovalVO;
import com.anyi.srb.entity.vo.BorrowerDetailVO;
import com.anyi.srb.entity.vo.BorrowerVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 借款人 服务类
 * </p>
 *
 * @author anyi
 * @since 2022-06-02
 */
public interface BorrowerService extends IService<Borrower> {

    void saveBorrower(BorrowerVO borrowerVO, Long userId);

    Integer checkStatus(Long userId);

    IPage<Borrower> listPage(Page<Borrower> pageParam, String keyword);

    BorrowerDetailVO show(Long id);

    void approval(BorrowerApprovalVO borrowerApprovalVO);

}

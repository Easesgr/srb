package com.anyi.srb.service.impl;

import com.anyi.srb.entity.BorrowerAttach;
import com.anyi.srb.mapper.BorrowerAttachMapper;
import com.anyi.srb.service.BorrowerAttachService;
import com.anyi.srb.entity.vo.BorrowerAttachVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 借款人上传资源表 服务实现类
 * </p>
 *
 * @author anyi
 * @since 2022-06-02
 */
@Service
public class BorrowerAttachServiceImpl extends ServiceImpl<BorrowerAttachMapper, BorrowerAttach> implements BorrowerAttachService {

    /**
     * 根据 borrowerId 获取所有的附件并且封装
     * @param borrowerId
     * @return
     */
    @Override
    public List<BorrowerAttachVO> getAttachVo(Long borrowerId) {
        List<BorrowerAttach> list = list(new LambdaQueryWrapper<BorrowerAttach>().
                eq(BorrowerAttach::getBorrowerId, borrowerId));
        List<BorrowerAttachVO> collect = list.stream().map(item -> {
            BorrowerAttachVO borrowerAttachVO = new BorrowerAttachVO();
            BeanUtils.copyProperties(item, borrowerAttachVO);
            return borrowerAttachVO;
        }).collect(Collectors.toList());
        return collect;
    }
}

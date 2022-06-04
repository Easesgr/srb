package com.anyi.srb.service.impl;

import com.anyi.srb.common.ResponseEnum;
import com.anyi.srb.entity.*;
import com.anyi.srb.enums.BorrowAuthEnum;
import com.anyi.srb.enums.BorrowInfoStatusEnum;
import com.anyi.srb.enums.UserBindEnum;
import com.anyi.srb.exception.BusinessException;
import com.anyi.srb.mapper.BorrowInfoMapper;
import com.anyi.srb.service.*;
import com.anyi.srb.entity.vo.BorrowInfoApprovalVO;
import com.anyi.srb.entity.vo.BorrowerDetailVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务实现类
 * </p>
 *
 * @author anyi
 * @since 2022-06-02
 */
@Service
public class BorrowInfoServiceImpl extends ServiceImpl<BorrowInfoMapper, BorrowInfo> implements BorrowInfoService {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private IntegralGradeService integralGradeService;

    @Resource
    private DictService dictService;

    @Resource
    private BorrowerService borrowerService;
    @Resource
    private LendService lendService;

    /**
     * 获取最大借款额度
     * @param userId
     * @return
     */
    @Override
    public BigDecimal getMoney(Long userId) {
        // 根据用户id 获取积分情况
        UserInfo userInfo = userInfoService.getById(userId);
        Integer integral = userInfo.getIntegral();
        // 根据积分情况获取最大额度
        LambdaQueryWrapper<IntegralGrade> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(IntegralGrade::getIntegralStart,integral )
                // 是数据库里面的值和外面的值做比较
                .ge(IntegralGrade::getIntegralEnd,integral);
        IntegralGrade integralGrade = integralGradeService.getOne(wrapper);
        // 返回信息
        return integralGrade.getBorrowAmount();
    }

    /**
     * 保存借款信息
     * @param borrowInfo
     * @param userId
     */
    @Override
    public void saveBorrowerInfo(BorrowInfo borrowInfo, Long userId) {
        // 根据id查询信息
        UserInfo userInfo = userInfoService.getById(userId);

        // 判断绑定状态
        if (userInfo.getBindStatus() != UserBindEnum.BIND_OK.getStatus()){
            throw new BusinessException(ResponseEnum.USER_NO_BIND_ERROR);
        }
        // 判断借款信息认证状态
        if (userInfo.getBorrowAuthStatus() != BorrowAuthEnum.AUTH_OK.getStatus()){
            throw new BusinessException(ResponseEnum.USER_NO_AMOUNT_ERROR);
        }
        // 判断额度是否超出

        if(borrowInfo.getAmount().doubleValue() > getMoney(userId).doubleValue()){
            throw new BusinessException(ResponseEnum.USER_AMOUNT_LESS_ERROR);
        }
        // 填写其他需要的信息将借款信息保存
        borrowInfo.setUserId(userId);
        // 年利率转化
        borrowInfo.setBorrowYearRate( borrowInfo.getBorrowYearRate().divide(new BigDecimal(100)));
        // 认证状态
        borrowInfo.setStatus(BorrowAuthEnum.AUTH_RUN.getStatus());

        save(borrowInfo);
    }

    /**
     * 获取当前借款信息审核状态
     * @return
     */
    @Override
    public Integer getStatus(Long userId) {
        BorrowInfo one = getOne(new LambdaQueryWrapper<BorrowInfo>()
                .eq(BorrowInfo::getUserId, userId));
        if (one == null){
            return 0;
        }
        return one.getStatus();
    }

    /**
     * 后台管理系统获取借款列表信息
     * @return
     */
    @Override
    public List<BorrowInfo> selectList() {
        // 查询基本信息
        List<BorrowInfo> borrowInfoList = baseMapper.selectBorrowInfoList();

        // 封装拓展信息
        borrowInfoList.forEach(borrowInfo -> {
            String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", borrowInfo.getReturnMethod());
            String moneyUse = dictService.getNameByParentDictCodeAndValue("moneyUse", borrowInfo.getMoneyUse());
            String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());
            borrowInfo.getParam().put("returnMethod", returnMethod);
            borrowInfo.getParam().put("moneyUse", moneyUse);
            borrowInfo.getParam().put("status", status);
        });

        // 返回信息
        return borrowInfoList;
    }

    /**
     * 获取详细信息
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> getBorrowInfoDetail(Long id) {
        //根据 id 查询 出借款信息
        BorrowInfo borrowInfo = getById(id);
        String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", borrowInfo.getReturnMethod());
        String moneyUse = dictService.getNameByParentDictCodeAndValue("moneyUse", borrowInfo.getMoneyUse());
        String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());
        borrowInfo.getParam().put("returnMethod", returnMethod);
        borrowInfo.getParam().put("moneyUse", moneyUse);
        borrowInfo.getParam().put("status", status);
        // 根据借款信息查询出用户信息
        Borrower borrower = borrowerService.getOne(new LambdaQueryWrapper<Borrower>()
                .eq(Borrower::getUserId, borrowInfo.getUserId()));

        BorrowerDetailVO borrow = borrowerService.show(borrower.getId());
        // 将信息封装成map对象返回
        Map<String, Object> map = new HashMap<>();
        map.put("borrower", borrow);
        map.put("borrowInfo", borrowInfo);
        return map;
    }

    /**
     * 审核信息
     * @param approvalVO
     */
    @Transactional
    @Override
    public void approval(BorrowInfoApprovalVO approvalVO) {
        // 根据审核信息，修改借款信息状态
        //修改借款信息状态
        Long borrowInfoId = approvalVO.getId();
        BorrowInfo borrowInfo = getById(borrowInfoId);
        borrowInfo.setStatus(approvalVO.getStatus());
        baseMapper.updateById(borrowInfo);

        //审核通过则创建标的
        if (approvalVO.getStatus().intValue() == BorrowInfoStatusEnum.CHECK_OK.getStatus().intValue()) {
            //创建标的
            //TODO
            lendService.createLend(approvalVO, borrowInfo);
        }
    }
}

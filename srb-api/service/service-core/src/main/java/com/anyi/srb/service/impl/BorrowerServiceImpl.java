package com.anyi.srb.service.impl;

import com.anyi.srb.entity.Borrower;
import com.anyi.srb.entity.BorrowerAttach;
import com.anyi.srb.entity.UserInfo;
import com.anyi.srb.entity.UserIntegral;
import com.anyi.srb.enums.BorrowerStatusEnum;
import com.anyi.srb.enums.IntegralEnum;
import com.anyi.srb.mapper.BorrowerMapper;
import com.anyi.srb.service.*;
import com.anyi.srb.entity.vo.BorrowerApprovalVO;
import com.anyi.srb.entity.vo.BorrowerAttachVO;
import com.anyi.srb.entity.vo.BorrowerDetailVO;
import com.anyi.srb.entity.vo.BorrowerVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 借款人 服务实现类
 * </p>
 *
 * @author anyi
 * @since 2022-06-02
 */
@Service
public class BorrowerServiceImpl extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private BorrowerAttachService attachService;


    @Resource
    private DictService dictService;

    @Resource
    private UserIntegralService integralService;



    // 保存借款信息
    @Override
    public void saveBorrower(BorrowerVO borrowerVO, Long userId) {
        // 根据id 查出借款人信息

        UserInfo userInfo = userInfoService.getById(userId);

        //保存借款人信息
        Borrower borrower = new Borrower();
        BeanUtils.copyProperties(borrowerVO, borrower);

        borrower.setUserId(userId);
        borrower.setName(userInfo.getName());
        borrower.setIdCard(userInfo.getIdCard());
        borrower.setMobile(userInfo.getMobile());
        borrower.setIsMarry(borrowerVO.getIsMarry());
        borrower.setStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());//认证中
        baseMapper.insert(borrower);

        // 取出附件信息并保存
        List<BorrowerAttach> borrowerAttachList = borrowerVO.getBorrowerAttachList();
        borrowerAttachList.forEach(item->{
            item.setBorrowerId(borrower.getId());
            attachService.save(item);
        });
        //更新会员状态，更新为认证中
        userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        userInfoService.updateById(userInfo);
    }

    @Override
    public Integer checkStatus(Long userId) {
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper.select("status").eq("user_id", userId);
        List<Object> objects = baseMapper.selectObjs(borrowerQueryWrapper);
        if(objects.size() == 0){
            //借款人尚未提交信息
            return BorrowerStatusEnum.NO_AUTH.getStatus();
        }
        return (Integer)objects.get(0);
    }
    @Override
    public IPage<Borrower> listPage(Page<Borrower> pageParam, String keyword) {

        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();

        if(StringUtils.isEmpty(keyword)){
            return baseMapper.selectPage(pageParam, null);
        }

        borrowerQueryWrapper.like("name", keyword)
                .or().like("id_card", keyword)
                .or().like("mobile", keyword)
                .orderByDesc("id");
        return baseMapper.selectPage(pageParam, borrowerQueryWrapper);
    }

    // 获取借款人详细信息
    @Override
    public BorrowerDetailVO show(Long id) {
        BorrowerDetailVO borrowerDetailVO = new BorrowerDetailVO();

        // 根据id查询出借款人基本信息
        Borrower borrower = getById(id);
        BeanUtils.copyProperties(borrower,borrowerDetailVO);

        // 设置状态

        //婚否
        borrowerDetailVO.setMarry(borrower.getIsMarry()?"是":"否");
        //性别
        borrowerDetailVO.setSex(borrower.getSex()==1?"男":"女");
        // 状态
        borrowerDetailVO.setStatus(BorrowerStatusEnum.getMsgByStatus(borrower.getStatus()));

        //计算下拉列表选中内容
        String education = dictService.getNameByParentDictCodeAndValue("education", borrower.getEducation());
        String industry = dictService.getNameByParentDictCodeAndValue("moneyUse", borrower.getIndustry());
        String income = dictService.getNameByParentDictCodeAndValue("income", borrower.getIncome());
        String returnSource = dictService.getNameByParentDictCodeAndValue("returnSource", borrower.getReturnSource());
        String contactsRelation = dictService.getNameByParentDictCodeAndValue("relation", borrower.getContactsRelation());

        borrowerDetailVO.setEducation(education);
        borrowerDetailVO.setIndustry(industry);
        borrowerDetailVO.setIncome(income);
        borrowerDetailVO.setReturnSource(returnSource);
        borrowerDetailVO.setContactsRelation(contactsRelation);

        // 根据借款id 查询出所有的附件，将附件封装
        Long borrowerId = borrower.getId();
        List<BorrowerAttachVO> borrowerAttachVOS = attachService.getAttachVo(borrowerId);
        borrowerDetailVO.setBorrowerAttachVOList(borrowerAttachVOS);

        return borrowerDetailVO;
    }

    /**
     * 对提交的借款信息进行审批
     * @param borrowerApprovalVO
     */
    @Override
    public void approval(BorrowerApprovalVO borrowerApprovalVO) {
        // 根据借款信息id 获取所有借款信息，方便后面修改状态
        Borrower borrower = getById(borrowerApprovalVO.getBorrowerId());

        Integer allIntegrals = 0;
        // 封装积分信息
        // 1. 基本信息积分
        Integer infoIntegral = borrowerApprovalVO.getInfoIntegral();
        UserIntegral userIntegral = new UserIntegral();
        userIntegral.setIntegral(infoIntegral);
        userIntegral.setUserId(borrower.getUserId());
        userIntegral.setContent(IntegralEnum.BORROWER_INFO.getMsg());
        integralService.save(userIntegral);
        allIntegrals +=  borrowerApprovalVO.getInfoIntegral();

        // 借款人身份信息
        if (borrowerApprovalVO.getIsCarOk()){
            userIntegral = new UserIntegral();
            userIntegral.setIntegral(IntegralEnum.BORROWER_IDCARD.getIntegral());
            userIntegral.setUserId(borrower.getUserId());
            userIntegral.setContent(IntegralEnum.BORROWER_IDCARD.getMsg());
            integralService.save(userIntegral);
            allIntegrals += IntegralEnum.BORROWER_IDCARD.getIntegral();
        }
        // 房屋信息
        if(borrowerApprovalVO.getIsHouseOk()){
            userIntegral = new UserIntegral();
            userIntegral.setIntegral(IntegralEnum.BORROWER_HOUSE.getIntegral());
            userIntegral.setUserId(borrower.getUserId());
            userIntegral.setContent(IntegralEnum.BORROWER_HOUSE.getMsg());
            integralService.save(userIntegral);
            allIntegrals += IntegralEnum.BORROWER_HOUSE.getIntegral();
        }

        // 车辆信息
        if(borrowerApprovalVO.getIsHouseOk()){
            userIntegral = new UserIntegral();
            userIntegral.setIntegral(IntegralEnum.BORROWER_CAR.getIntegral());
            userIntegral.setUserId(borrower.getUserId());
            userIntegral.setContent(IntegralEnum.BORROWER_CAR.getMsg());
            integralService.save(userIntegral);
            allIntegrals += IntegralEnum.BORROWER_CAR.getIntegral();
        }
        // 改变用户的积分信息
        UserInfo user = userInfoService.getById(borrower.getUserId());
        user.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_OK.getStatus());
        user.setIntegral(allIntegrals);
        userInfoService.updateById(user);
        // 修改审核状态
        borrower.setStatus(BorrowerStatusEnum.AUTH_OK.getStatus());
        updateById(borrower);
    }
}

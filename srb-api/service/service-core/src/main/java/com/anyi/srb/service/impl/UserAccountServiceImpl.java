package com.anyi.srb.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.anyi.srb.common.ResponseEnum;
import com.anyi.srb.entity.TransFlow;
import com.anyi.srb.entity.UserAccount;
import com.anyi.srb.entity.UserInfo;
import com.anyi.srb.entity.bo.TransFlowBO;
import com.anyi.srb.enums.TransTypeEnum;
import com.anyi.srb.exception.BusinessException;
import com.anyi.srb.hfb.FormHelper;
import com.anyi.srb.hfb.HfbConst;
import com.anyi.srb.hfb.RequestHelper;
import com.anyi.srb.mapper.UserAccountMapper;
import com.anyi.srb.service.TransFlowService;
import com.anyi.srb.service.UserAccountService;
import com.anyi.srb.service.UserBindService;
import com.anyi.srb.service.UserInfoService;
import com.anyi.srb.utils.LendNoUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author anyi
 * @since 2022-05-31
 */
@Service
@Slf4j
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {


    @Resource
    private UserBindService userBindService;

    @Resource
    private UserAccountService userAccountService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private TransFlowService transFlowService;
    /**
     * 充值
     * @param chargeAmt
     * @param userId
     * @return
     */
    @Override
    public String commitCharge(BigDecimal chargeAmt, Long userId) {
        // 根据userId 查询出用户信息
        UserInfo userInfo = userInfoService.getById(userId);
        String bindCode = userInfo.getBindCode();
        //判断账户绑定状态
        if(bindCode == null){
            throw new BusinessException(ResponseEnum.USER_NO_BIND_ERROR);
        }

        // 封装信息
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentBillNo", LendNoUtils.getNo());
        paramMap.put("bindCode", bindCode);
        paramMap.put("chargeAmt", chargeAmt);
        paramMap.put("feeAmt", new BigDecimal("0"));
        paramMap.put("notifyUrl", HfbConst.RECHARGE_NOTIFY_URL);//检查常量是否正确
        paramMap.put("returnUrl", HfbConst.RECHARGE_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);

        // 返回前端跳转表单信息
        String formStr = FormHelper.buildForm(HfbConst.RECHARGE_URL, paramMap);

        return formStr;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String notify(Map<String, Object> paramMap) {

        log.info("充值成功：" + JSONObject.toJSONString(paramMap));

        String bindCode = (String)paramMap.get("bindCode"); //充值人绑定协议号
        String chargeAmt = (String)paramMap.get("chargeAmt"); //充值金额

        //优化
        baseMapper.updateAccount(bindCode, new BigDecimal(chargeAmt), new BigDecimal(0));

        //增加交易流水
        String agentBillNo = (String)paramMap.get("agentBillNo"); //商户充值订单号
        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo,
                bindCode,
                new BigDecimal(chargeAmt),
                TransTypeEnum.RECHARGE,
                "充值");
        transFlowService.saveTransFlow(transFlowBO);



        return "success";
    }

    /**
     * 获取账户余额
     * @param userId
     * @return
     */
    @Override
    public BigDecimal getCount(Long userId) {
        UserAccount one = getOne(new LambdaQueryWrapper<UserAccount>()
                .eq(UserAccount::getUserId, userId));
        BigDecimal amount = one.getAmount();
        return amount;
    }

    @Override
    public String commitWithdraw(BigDecimal fetchAmt, Long userId) {

        //账户可用余额充足：当前用户的余额 >= 当前用户的提现金额
        BigDecimal amount = userAccountService.getCount(userId);//获取当前用户的账户余额
        if (amount.doubleValue() <= fetchAmt.doubleValue()){
            throw new BusinessException(ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);
        }

        String bindCode = userBindService.getBindCodeByUserId(userId);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentBillNo", LendNoUtils.getWithdrawNo());
        paramMap.put("bindCode", bindCode);
        paramMap.put("fetchAmt", fetchAmt);
        paramMap.put("feeAmt", new BigDecimal(0));
        paramMap.put("notifyUrl", HfbConst.WITHDRAW_NOTIFY_URL);
        paramMap.put("returnUrl", HfbConst.WITHDRAW_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);

        //构建自动提交表单
        String formStr = FormHelper.buildForm(HfbConst.WITHDRAW_URL, paramMap);
        return formStr;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void notifyWithdraw(Map<String, Object> paramMap) {

        log.info("提现成功");
        String agentBillNo = (String)paramMap.get("agentBillNo");
        boolean result = transFlowService.isSaveTransFlow(agentBillNo);
        if(result){
            log.warn("幂等性返回");
            return;
        }

        String bindCode = (String)paramMap.get("bindCode");
        String fetchAmt = (String)paramMap.get("fetchAmt");

        //根据用户账户修改账户金额
        baseMapper.updateAccount(bindCode, new BigDecimal("-" + fetchAmt), new BigDecimal(0));

        //增加交易流水
        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo,
                bindCode,
                new BigDecimal(fetchAmt),
                TransTypeEnum.WITHDRAW,
                "提现");
        transFlowService.saveTransFlow(transFlowBO);
    }
}

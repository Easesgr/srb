package com.anyi.srb.service.impl;

import com.anyi.srb.common.ResponseEnum;
import com.anyi.srb.entity.UserBind;
import com.anyi.srb.entity.UserInfo;
import com.anyi.srb.enums.UserBindEnum;
import com.anyi.srb.exception.BusinessException;
import com.anyi.srb.hfb.FormHelper;
import com.anyi.srb.hfb.HfbConst;
import com.anyi.srb.hfb.RequestHelper;
import com.anyi.srb.mapper.UserBindMapper;
import com.anyi.srb.service.UserBindService;
import com.anyi.srb.service.UserInfoService;
import com.anyi.srb.entity.vo.UserBindVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务实现类
 * </p>
 *
 * @author anyi
 * @since 2022-05-31
 */
@Service
public class UserBindServiceImpl extends ServiceImpl<UserBindMapper, UserBind> implements UserBindService {

    @Resource
    private UserInfoService userInfoService;


    /**
     * 将绑定到汇付宝
     * @param userBindVO
     * @param userId
     * @return
     */
    @Override
    public String formString(UserBindVO userBindVO, Long userId) {
        //查询身份证号码是否绑定
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper
                .eq("id_card", userBindVO.getIdCard())
                .ne("user_id", userId);
        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);
        //USER_BIND_IDCARD_EXIST_ERROR(-301, "身份证号码已绑定"),
        if (userBind != null){
            throw new BusinessException(ResponseEnum.USER_BIND_IDCARD_EXIST_ERROR);
        }
        //查询用户绑定信息
        userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id", userId);
        userBind = baseMapper.selectOne(userBindQueryWrapper);

        //判断是否有绑定记录
        if(userBind == null) {
            //如果未创建绑定记录，则创建一条记录
            userBind = new UserBind();
            BeanUtils.copyProperties(userBindVO, userBind);
            userBind.setUserId(userId);
            userBind.setStatus(UserBindEnum.NO_BIND.getStatus());
            baseMapper.insert(userBind);
        } else {
            //曾经跳转到托管平台，但是未操作完成，此时将用户最新填写的数据同步到userBind对象
            BeanUtils.copyProperties(userBindVO, userBind);
            baseMapper.updateById(userBind);
        }

        // 按照文档封装信息
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentUserId", userId);
        paramMap.put("idCard",userBindVO.getIdCard());
        paramMap.put("personalName", userBindVO.getName());
        paramMap.put("bankType", userBindVO.getBankType());
        paramMap.put("bankNo", userBindVO.getBankNo());
        paramMap.put("mobile", userBindVO.getMobile());
        paramMap.put("returnUrl", HfbConst.USERBIND_RETURN_URL);
        paramMap.put("notifyUrl", HfbConst.USERBIND_NOTIFY_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        // 将所有信息通过 | 连接起来经过md5加密后生成签名
        paramMap.put("sign", RequestHelper.getSign(paramMap));

        //构建充值自动提交表单
        String formStr = FormHelper.buildForm(HfbConst.USERBIND_URL, paramMap);

        return formStr;
    }

    /**
     * 修改用户绑定状态
     * @param map
     */
    @Override
    public void notify(Map<String, Object> map) {
        // userId
        String  agentUserId =(String) map.get("agentUserId");
        // bind_code
        String  bindCode = (String) map.get("bindCode");

        // 1. 修改bind表中的 bind_code 和 status
        UserBind bind = getOne(new LambdaQueryWrapper<UserBind>()
                .eq(UserBind::getUserId,agentUserId));
        bind.setBindCode(bindCode);
        bind.setStatus(UserBindEnum.BIND_OK.getStatus());
        updateById(bind);

        // 2. 修改info表中的 bind_code 和 status

        UserInfo userInfo = userInfoService.getOne(new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getId, agentUserId));
        userInfo.setStatus(UserBindEnum.BIND_OK.getStatus());
        userInfo.setBindCode(bindCode);
        userInfo.setBindStatus(UserBindEnum.BIND_OK.getStatus());
        userInfo.setName(bind.getName());
        userInfo.setIdCard(bind.getIdCard());
        userInfoService.updateById(userInfo);
    }

    /**
     * 根据id 号获取bincode
     * @param investUserId
     * @return
     */
    @Override
    public String getBindCodeByUserId(Long investUserId) {
        UserBind one = getOne(new LambdaQueryWrapper<UserBind>().eq(UserBind::getUserId, investUserId));
        return one.getBindCode();
    }
}

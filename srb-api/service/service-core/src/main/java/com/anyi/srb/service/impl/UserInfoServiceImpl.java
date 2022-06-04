package com.anyi.srb.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.anyi.srb.common.RedisField;
import com.anyi.srb.common.ResponseEnum;
import com.anyi.srb.entity.UserAccount;
import com.anyi.srb.entity.UserInfo;
import com.anyi.srb.entity.UserLoginRecord;
import com.anyi.srb.entity.vo.UserIndexVO;
import com.anyi.srb.exception.BusinessException;
import com.anyi.srb.mapper.UserAccountMapper;
import com.anyi.srb.mapper.UserInfoMapper;
import com.anyi.srb.entity.query.UserInfoQuery;
import com.anyi.srb.mapper.UserLoginRecordMapper;
import com.anyi.srb.service.UserAccountService;
import com.anyi.srb.service.UserInfoService;
import com.anyi.srb.service.UserLoginRecordService;
import com.anyi.srb.utils.JwtUtils;
import com.anyi.srb.entity.vo.LoginVO;
import com.anyi.srb.entity.vo.RegisterVO;
import com.anyi.srb.entity.vo.UserInfoVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author anyi
 * @since 2022-05-31
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    private UserAccountService userAccountService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserLoginRecordService userLoginRecordService;

    @Resource
    private UserAccountMapper userAccountMapper;

    @Resource
    private UserLoginRecordMapper userLoginRecordMapper;


    /**
     * 注册功能
     * @param registerVO
     */
    @Override
    public void register(RegisterVO registerVO) {
        // 判断手机号是否已经注册
        UserInfo one = getOne(new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getMobile, registerVO.getMobile()));
        if (one !=null){
            throw new BusinessException(ResponseEnum.MOBILE_EXIST_ERROR);
        }
        // 存入信息
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(registerVO,userInfo);
        userInfo.setPassword(SecureUtil.md5(registerVO.getPassword()));
        userInfo.setHeadImg("https://edu-teacher-120.oss-cn-hangzhou.aliyuncs.com/2022/05/13/dd960afc-c79e-45f5-87e2-32c82d99f70efile.png");
        userInfo.setName("安逸");
        userInfo.setNickName("安逸i");
        userInfo.setStatus(UserInfo.STATUS_NORMAL);
        save(userInfo);

        // 保存会员信息
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userInfo.getId());
        userAccountService.save(userAccount);
    }

    /**
     * 登录
     * @param loginVO
     * @param ip
     */
    @Override
    public UserInfoVO login(LoginVO loginVO, String ip) {
        LambdaQueryWrapper<UserInfo> wr = new LambdaQueryWrapper<>();
        wr.eq(UserInfo::getMobile,loginVO.getMobile());
        wr.eq(UserInfo::getUserType,loginVO.getUserType());
        UserInfo one = getOne(wr);
        // 用户是否存在
        if (one == null){
            throw new BusinessException(ResponseEnum.LOGIN_MOBILE_ERROR);
        }
        // 是否为锁定状态
        if (one.getStatus() == UserInfo.STATUS_LOCKED){
            throw new BusinessException(ResponseEnum.LOGIN_LOKED_ERROR);
        }
        // 密码是否正确
        if (!one.getPassword().equals(SecureUtil.md5(loginVO.getPassword()))){
            throw new BusinessException(ResponseEnum.LOGIN_PASSWORD_ERROR);
        }

        // 保存登录日志
        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setIp(ip);
        userLoginRecord.setUserId(one.getId());
        userLoginRecordService.save(userLoginRecord);

        // 封装userInfoVo对象返回
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(one, userInfoVO);

        // 生成token
        String token = JwtUtils.createToken(one.getId(), one.getName());

        userInfoVO.setToken(token);
        // token 保存到redis中
        redisTemplate.opsForValue().set(RedisField.USER_TOKEN + one.getMobile(), token,30, TimeUnit.MINUTES);
        return userInfoVO;
    }

    /**
     * 分页查找
     * @param pageParam
     * @param userInfoQuery
     * @return
     */
    @Override
    public IPage<UserInfo> listPage(Page<UserInfo> pageParam, UserInfoQuery userInfoQuery) {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(userInfoQuery.getUserType() !=null,UserInfo::getUserType,userInfoQuery.getUserType())
                .eq(userInfoQuery.getMobile() !=null,UserInfo::getMobile,userInfoQuery.getMobile())
                .eq(userInfoQuery.getStatus() !=null,UserInfo::getStatus,userInfoQuery.getStatus());

        Page<UserInfo> page = page(pageParam, wrapper);
        return page;
    }

    @Override
    public UserIndexVO getIndexUserInfo(Long userId) {

        //用户信息
        UserInfo userInfo = baseMapper.selectById(userId);

        //账户信息
        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id", userId);
        UserAccount userAccount = userAccountMapper.selectOne(userAccountQueryWrapper);

        //登录信息
        QueryWrapper<UserLoginRecord> userLoginRecordQueryWrapper = new QueryWrapper<>();
        userLoginRecordQueryWrapper
                .eq("user_id", userId)
                .orderByDesc("id")
                .last("limit 1");
        UserLoginRecord userLoginRecord = userLoginRecordMapper.selectOne(userLoginRecordQueryWrapper);
        //组装结果数据
        UserIndexVO userIndexVO = new UserIndexVO();
        userIndexVO.setUserId(userInfo.getId());
        userIndexVO.setUserType(userInfo.getUserType());
        userIndexVO.setName(userInfo.getName());
        userIndexVO.setNickName(userInfo.getNickName());
        userIndexVO.setHeadImg(userInfo.getHeadImg());
        userIndexVO.setBindStatus(userInfo.getBindStatus());
        userIndexVO.setAmount(userAccount.getAmount());
        userIndexVO.setFreezeAmount(userAccount.getFreezeAmount());
        userIndexVO.setLastLoginTime(userLoginRecord.getCreateTime());

        return userIndexVO;
    }
}

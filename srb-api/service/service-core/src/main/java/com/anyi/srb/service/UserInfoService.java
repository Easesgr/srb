package com.anyi.srb.service;

import com.anyi.srb.entity.UserInfo;
import com.anyi.srb.entity.query.UserInfoQuery;
import com.anyi.srb.entity.vo.LoginVO;
import com.anyi.srb.entity.vo.RegisterVO;
import com.anyi.srb.entity.vo.UserIndexVO;
import com.anyi.srb.entity.vo.UserInfoVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author anyi
 * @since 2022-05-31
 */
public interface UserInfoService extends IService<UserInfo> {

    void register(RegisterVO registerVO);

    UserInfoVO login(LoginVO loginVO, String ip);

    IPage<UserInfo> listPage(Page<UserInfo> pageParam, UserInfoQuery userInfoQuery);

    UserIndexVO getIndexUserInfo(Long userId);
}

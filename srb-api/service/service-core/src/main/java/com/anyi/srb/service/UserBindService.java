package com.anyi.srb.service;

import com.anyi.srb.entity.UserBind;
import com.anyi.srb.entity.vo.UserBindVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务类
 * </p>
 *
 * @author anyi
 * @since 2022-05-31
 */
public interface UserBindService extends IService<UserBind> {

    String formString(UserBindVO userBindVO, Long userId);

    void notify(Map<String, Object> map);

    String getBindCodeByUserId(Long investUserId);
}

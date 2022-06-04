package com.anyi.srb.service.impl;

import com.anyi.srb.entity.UserLoginRecord;
import com.anyi.srb.mapper.UserLoginRecordMapper;
import com.anyi.srb.service.UserLoginRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户登录记录表 服务实现类
 * </p>
 *
 * @author anyi
 * @since 2022-05-31
 */
@Service
public class UserLoginRecordServiceImpl extends ServiceImpl<UserLoginRecordMapper, UserLoginRecord> implements UserLoginRecordService {

}

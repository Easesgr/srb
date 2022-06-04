package com.anyi.sms.service;

import com.anyi.sms.service.impl.UserServiceImpl;
import com.anyi.srb.common.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 安逸i
 * @version 1.0
 */
@FeignClient(value = "service-core",fallback = UserServiceImpl.class)
public interface UserService {
    @GetMapping("/srb/front/user/info/checkMoblie/{mobile}")
    public boolean checkMobile(@PathVariable String mobile);
}

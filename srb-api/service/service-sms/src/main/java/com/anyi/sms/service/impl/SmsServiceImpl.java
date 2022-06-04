package com.anyi.sms.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.anyi.sms.common.SmsField;
import com.anyi.sms.service.SmsService;
import com.anyi.sms.service.UserService;
import com.anyi.srb.common.RedisField;
import com.anyi.srb.common.ResponseEnum;
import com.anyi.srb.exception.BusinessException;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author 安逸i
 * @version 1.0
 */
@Service
@Slf4j
public class SmsServiceImpl implements SmsService {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private UserService userService;
    @Override
    public void sendCode(String phone) {
        try {

            boolean b = userService.checkMobile(phone);
            if (b){
                throw new BusinessException(ResponseEnum.MOBILE_EXIST_ERROR);
            }
            Credential cred = new Credential(SmsField.KEY_ID,SmsField.KEY_SECRET);

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setReqMethod("POST");
            httpProfile.setConnTimeout(60);
            httpProfile.setEndpoint("sms.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setSignMethod("HmacSHA256");
            clientProfile.setHttpProfile(httpProfile);
            SmsClient client = new SmsClient(cred, "ap-guangzhou",clientProfile);
            SendSmsRequest req = new SendSmsRequest();

            String appid = SmsField.SDK_ID;
            req.setSmsSdkAppid(appid);

            String sign = SmsField.SIGN_NAME;
            req.setSign(sign);

            String senderid = "";
            req.setSenderId(senderid);

            String session = "";
            req.setSessionContext(session);

            String extendcode = "";
            req.setExtendCode(extendcode);

            String templateID = SmsField.TEMPLATE_CODE;
            req.setTemplateID(templateID);

            String code = RandomUtil.randomNumbers(6);

            String[] templateParams = {code};

            // SendSmsResponse res = client.SendSms(req);
            redisTemplate.opsForValue().set(RedisField.PHONE_CODE + phone, code,3, TimeUnit.MINUTES);
            phone = "+86" + phone;
            String[] phoneNumbers = {phone};
            req.setPhoneNumberSet(phoneNumbers);
            req.setTemplateParamSet(templateParams);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

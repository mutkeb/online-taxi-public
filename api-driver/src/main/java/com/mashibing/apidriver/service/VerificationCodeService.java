package com.mashibing.apidriver.service;

import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.request.VerificationCodeDTO;
import org.springframework.stereotype.Service;

@Service
public class VerificationCodeService {

    /**
     * 检查并发送验证码
     * @param driverPhone
     * @return
     */
    public ResponseResult checkAndSendVerificationCode(String driverPhone){
        //  调用service-driver-user，该手机号的司机是否存在

        //  获取验证码

        //  调用第三方发送验证码

        //  存入Redis
        return ResponseResult.success();
    }
}

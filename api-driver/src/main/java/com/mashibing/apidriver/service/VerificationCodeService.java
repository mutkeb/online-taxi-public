package com.mashibing.apidriver.service;

import com.mashibing.apidriver.remote.ServiceDriverUserClient;
import com.mashibing.apidriver.remote.ServiceVerificationCodeClient;
import com.mashibing.internalcommon.constant.CommonStatusEnum;
import com.mashibing.internalcommon.constant.DriverCarConstant;
import com.mashibing.internalcommon.constant.IdentityConstant;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.request.VerificationCodeDTO;
import com.mashibing.internalcommon.response.DriverUserExistsResponse;
import com.mashibing.internalcommon.response.NumberCodeResponse;
import com.mashibing.internalcommon.util.RedisPrefixUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class VerificationCodeService {

    @Autowired
    private ServiceDriverUserClient serviceDriverUserClient;

    @Autowired
    private ServiceVerificationCodeClient serviceVerificationCodeClient;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 检查并发送验证码
     * @param driverPhone
     * @return
     */
    public ResponseResult checkAndSendVerificationCode(String driverPhone){
        //  调用service-driver-user，该手机号的司机是否存在
        ResponseResult<DriverUserExistsResponse> user = serviceDriverUserClient.getUser(driverPhone);
        DriverUserExistsResponse data = user.getData();
        int ifExists = data.getIfExists();
        if (ifExists != DriverCarConstant.DRIVER_EXISTS){
            return ResponseResult.fail(CommonStatusEnum.DRIVER_NOT_EXISTS.getCode(),CommonStatusEnum.DRIVER_NOT_EXISTS.getValue());
        }
        log.info(driverPhone + "的司机存在");
        //  获取验证码
        ResponseResult<NumberCodeResponse> numberCodeResult = serviceVerificationCodeClient.numberCode(6);
        NumberCodeResponse numberCodeResponse = numberCodeResult.getData();
        int numberCode = numberCodeResponse.getNumberCode();
        log.info("验证码：" + numberCode);
        //  调用第三方发送验证码
        //  存入Redis
        String key = RedisPrefixUtils.generateKeyByPhone(driverPhone, IdentityConstant.DRIVER_IDENTITY);
        redisTemplate.opsForValue().set(key,numberCode + "",2, TimeUnit.MINUTES);
        return ResponseResult.success();
    }
}

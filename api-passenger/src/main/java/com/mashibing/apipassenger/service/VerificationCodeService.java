package com.mashibing.apipassenger.service;


import com.mashibing.apipassenger.remote.ServiceVerificationcodeClient;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.response.NumberCodeResponse;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {

    @Autowired
    private ServiceVerificationcodeClient serviceVerificationcodeClient;

    @Autowired
    private RedisTemplate redisTemplate;

    //  乘客验证码的前缀
    private String verificationCodePrefix = "passenger-verification-code-";

    //  生成验证码
    public ResponseResult generateCode(String passengerPhone){
        //  调用验证码服务，获取验证码
        System.out.println("调用验证码服务，获取验证码");

        ResponseResult<NumberCodeResponse> numberCodeResponse = serviceVerificationcodeClient.getNumberCode(6);
        int numberCode = numberCodeResponse.getData().getNumberCode();

        System.out.println("remote number code:" +numberCode);

        //   存入Redis
        System.out.println("存入Redis");
        //  key,value,过期时间
        String key = verificationCodePrefix + passengerPhone;
        redisTemplate.opsForValue().set(key,numberCode,2, TimeUnit.MINUTES);

        //  通过短信服务商，将对应的验证码发送到手机上
        
        return ResponseResult.success();
    }
}

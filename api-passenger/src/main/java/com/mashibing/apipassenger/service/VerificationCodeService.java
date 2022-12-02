package com.mashibing.apipassenger.service;


import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class VerificationCodeService {

    //  生成验证码
    public String generateCode(String passengerPhone){
        //  调用验证码服务，获取验证码
        System.out.println("调用验证码服务，获取验证码");
        String code = "1111";

        //   存入Redis
        System.out.println("存入Redis");

        //  包装一个返回值
        JSONObject result = new JSONObject();
        result.put("code",1);
        result.put("message","success");
        return result.toString();
    }
}

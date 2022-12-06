package com.mashibing.apipassenger.service;


import com.mashibing.apipassenger.remote.ServicePassengerUserClient;
import com.mashibing.apipassenger.remote.ServiceVerificationcodeClient;
import com.mashibing.internalcommon.constant.CommonStatusEnum;
import com.mashibing.internalcommon.constant.IdentityConstant;
import com.mashibing.internalcommon.constant.TokenConstant;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.request.VerificationCodeDTO;
import com.mashibing.internalcommon.response.NumberCodeResponse;
import com.mashibing.internalcommon.response.TokenResponse;
import com.mashibing.internalcommon.util.JwtUtils;
import com.mashibing.internalcommon.util.RedisPrefixUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {

    @Autowired
    private ServiceVerificationcodeClient serviceVerificationcodeClient;

    @Autowired
    private ServicePassengerUserClient servicePassengerUserClient;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 生成验证码
     * @param passengerPhone
     * @return
     */
    public ResponseResult generateCode(String passengerPhone){
        //  调用验证码服务，获取验证码
        System.out.println("调用验证码服务，获取验证码");

        ResponseResult<NumberCodeResponse> numberCodeResponse = serviceVerificationcodeClient.getNumberCode(6);
        int numberCode = numberCodeResponse.getData().getNumberCode();

        System.out.println("remote number code:" +numberCode);

        //   存入Redis
        System.out.println("存入Redis");
        //  key,value,过期时间
        String key = RedisPrefixUtils.generateKeyByPhone(passengerPhone);
        redisTemplate.opsForValue().set(key,numberCode + "",2, TimeUnit.MINUTES);
        //  通过短信服务商，将对应的验证码发送到手机上
        
        return ResponseResult.success();
    }




    /**
     * 校验验证码
     * @param passengerPhone 手机号
     * @param verificationCode 验证码
     * @return
     */


    public ResponseResult checkCode(String passengerPhone, String verificationCode){
        //  根据手机号，去redis读取验证码
        System.out.println("根据手机号，去redis读取验证码");


        //  生成key
        String key = RedisPrefixUtils.generateKeyByPhone(passengerPhone);

        //  根据key寻找value
        String codeRedis = redisTemplate.opsForValue().get(key).toString();
        System.out.println("redis中的value:" + codeRedis);

        //  检验验证码
        System.out.println("检验验证码");
        if (StringUtils.isBlank(codeRedis)){
            return ResponseResult.fail(CommonStatusEnum.VERIFICATION_CODE_ERROR.getCode(),CommonStatusEnum.VERIFICATION_CODE_ERROR.getValue());
        }

        if (!verificationCode.trim().equals(codeRedis.trim())){
            return ResponseResult.fail(CommonStatusEnum.VERIFICATION_CODE_ERROR.getCode(),CommonStatusEnum.VERIFICATION_CODE_ERROR.getValue());
        }
        //  判断原来是否有用户，并进行响应的处理
        VerificationCodeDTO verificationCodeDTO = new VerificationCodeDTO();
        verificationCodeDTO.setPassengerPhone(passengerPhone);
        servicePassengerUserClient.loginOrRegister(verificationCodeDTO);

        //  颁发令牌,不应使用魔法值，用常量
        String accessToken = JwtUtils.generatorToken(passengerPhone, IdentityConstant.PASSENGER_IDENTITY, TokenConstant.ACCESS_TOKEN_TYPE);
        String refreshToken = JwtUtils.generatorToken(passengerPhone,IdentityConstant.PASSENGER_IDENTITY,TokenConstant.REFRESH_TOKEN_TYPE);



        //  将accessToken保存到Redis中
        String accessTokenKey = RedisPrefixUtils.generatorTokenKey(passengerPhone, IdentityConstant.PASSENGER_IDENTITY,TokenConstant.ACCESS_TOKEN_TYPE);
        //  有效时间30天
        redisTemplate.opsForValue().set(accessTokenKey,accessToken,10,TimeUnit.SECONDS);

        //  将token保存到Redis中
        String refreshTokenKey = RedisPrefixUtils.generatorTokenKey(passengerPhone, IdentityConstant.PASSENGER_IDENTITY,TokenConstant.REFRESH_TOKEN_TYPE);
        //  有效时间30天
        redisTemplate.opsForValue().set(refreshTokenKey,refreshToken,200,TimeUnit.SECONDS);

        //  响应
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        return ResponseResult.success().setData(tokenResponse);
    }
}

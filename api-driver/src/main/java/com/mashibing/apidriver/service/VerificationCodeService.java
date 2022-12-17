package com.mashibing.apidriver.service;

import com.mashibing.apidriver.remote.ServiceDriverUserClient;
import com.mashibing.apidriver.remote.ServiceVerificationCodeClient;
import com.mashibing.internalcommon.constant.CommonStatusEnum;
import com.mashibing.internalcommon.constant.DriverCarConstant;
import com.mashibing.internalcommon.constant.IdentityConstant;
import com.mashibing.internalcommon.constant.TokenConstant;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.request.VerificationCodeDTO;
import com.mashibing.internalcommon.response.DriverUserExistsResponse;
import com.mashibing.internalcommon.response.NumberCodeResponse;
import com.mashibing.internalcommon.response.TokenResponse;
import com.mashibing.internalcommon.util.JwtUtils;
import com.mashibing.internalcommon.util.RedisPrefixUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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

    /**
     * 校验验证码
     * @param driverPhone 手机号
     * @param verificationCode 验证码
     * @return
     */
    public ResponseResult checkCode(String driverPhone, String verificationCode){
        //  根据手机号，去redis读取验证码
        System.out.println("根据手机号，去redis读取验证码");


        //  生成key
        String key = RedisPrefixUtils.generateKeyByPhone(driverPhone,IdentityConstant.DRIVER_IDENTITY);

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

        //  颁发令牌,不应使用魔法值，用常量
        String accessToken = JwtUtils.generatorToken(driverPhone, IdentityConstant.DRIVER_IDENTITY, TokenConstant.ACCESS_TOKEN_TYPE);
        String refreshToken = JwtUtils.generatorToken(driverPhone,IdentityConstant.DRIVER_IDENTITY,TokenConstant.REFRESH_TOKEN_TYPE);



        //  将accessToken保存到Redis中
        String accessTokenKey = RedisPrefixUtils.generatorTokenKey(driverPhone, IdentityConstant.DRIVER_IDENTITY,TokenConstant.ACCESS_TOKEN_TYPE);
        //  有效时间30天
        redisTemplate.opsForValue().set(accessTokenKey,accessToken,30,TimeUnit.DAYS);

        //  将token保存到Redis中
        String refreshTokenKey = RedisPrefixUtils.generatorTokenKey(driverPhone, IdentityConstant.DRIVER_IDENTITY,TokenConstant.REFRESH_TOKEN_TYPE);
        //  有效时间30天
        redisTemplate.opsForValue().set(refreshTokenKey,refreshToken,31,TimeUnit.DAYS);

        //  响应
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        return ResponseResult.success().setData(tokenResponse);
    }
}

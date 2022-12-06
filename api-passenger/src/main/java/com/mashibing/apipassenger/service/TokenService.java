package com.mashibing.apipassenger.service;


import com.mashibing.internalcommon.constant.CommonStatusEnum;
import com.mashibing.internalcommon.constant.TokenConstant;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.dto.TokenResult;
import com.mashibing.internalcommon.response.TokenResponse;
import com.mashibing.internalcommon.util.JwtUtils;
import com.mashibing.internalcommon.util.RedisPrefixUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    @Autowired
    RedisTemplate redisTemplate;


    public ResponseResult refreshToken(String refreshTokenSrc){
        //  解析refreshToken
        TokenResult tokenResult = JwtUtils.checkToken(refreshTokenSrc);
        if (tokenResult == null){
            return ResponseResult.fail(CommonStatusEnum.TOKEN_ERROR.getCode(),CommonStatusEnum.TOKEN_ERROR.getValue());
        }
        String phone = tokenResult.getPhone();
        String identity = tokenResult.getIdentity();

        //  去读取redis中的refreshToken
        String refreshTokenKey = RedisPrefixUtils.generatorTokenKey(phone, identity, TokenConstant.REFRESH_TOKEN_TYPE);
        String refreshTokenRedis = redisTemplate.opsForValue().get(refreshTokenKey).toString();

        //  检验refreshToken
        if ((StringUtils.isBlank(refreshTokenRedis)) || (!refreshTokenRedis.trim().equals(refreshTokenSrc.trim()))){
            return ResponseResult.fail(CommonStatusEnum.TOKEN_ERROR.getCode(),CommonStatusEnum.TOKEN_ERROR.getValue());
        }

        //  生成双Token
        String refreshToken = JwtUtils.generatorToken(phone, identity, TokenConstant.REFRESH_TOKEN_TYPE);
        String accessToken = JwtUtils.generatorToken(phone, identity, TokenConstant.ACCESS_TOKEN_TYPE);

        String accessTokenKey = RedisPrefixUtils.generatorTokenKey(phone,identity,TokenConstant.ACCESS_TOKEN_TYPE);

        //  TODO
//        redisTemplate.opsForValue().set(accessTokenKey,accessToken,30, TimeUnit.DAYS);
//        redisTemplate.opsForValue().set(refreshTokenKey,refreshToken,31, TimeUnit.DAYS);

        redisTemplate.opsForValue().set(accessTokenKey,accessToken,10, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(refreshTokenKey,refreshToken,200, TimeUnit.SECONDS);

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        return ResponseResult.success(tokenResponse);
    }
}

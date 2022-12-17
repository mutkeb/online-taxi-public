package com.mashibing.apidriver.interceptor;


import com.mashibing.internalcommon.constant.TokenConstant;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.dto.TokenResult;
import com.mashibing.internalcommon.util.JwtUtils;
import com.mashibing.internalcommon.util.RedisPrefixUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        boolean result = true;
        String resultString = "";

        String token = request.getHeader("Authorization");
        TokenResult tokenResult = JwtUtils.checkToken(token);
        if (tokenResult == null){
            resultString = "token invalid";
            result = false;
        }else{
            //  拼接key
            String phone = tokenResult.getPhone();
            String identity = tokenResult.getIdentity();

            String tokenKey = RedisPrefixUtils.generatorTokenKey(phone,identity, TokenConstant.ACCESS_TOKEN_TYPE);
            //  从redis中取出token
            String tokenRedis  = redisTemplate.opsForValue().get(tokenKey).toString();
            if ((StringUtils.isBlank(tokenRedis)) || (!token.trim().equals(tokenRedis.trim()))){
                resultString = "token invalid";
                result = false;
            }
        }

        if (!result){
            PrintWriter out = response.getWriter();
            //  转json格式打印
            out.print(JSONObject.fromObject(ResponseResult.fail(resultString)).toString());
        }

        return result;
    }
}

package com.mashibing.serviceverificationcode.controller;


import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.response.NumberCodeResponse;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NumberCodeController {

    @GetMapping("/numberCode/{size}")
    public ResponseResult numberCode(@PathVariable("size") int size){
        System.out.println("size:" + size);
        //  生成验证码
        //  获取随机数,利用小数点往后移就可以了，就是相当于乘
        double mathRandom = (Math.random() * 9 + 1 ) * Math.pow(10,size-1);
        int resultInt = (int)mathRandom;
        System.out.println("generate src code:"+ resultInt);

        //  定义返回值
        NumberCodeResponse response = new NumberCodeResponse();
        response.setNumberCode(resultInt);

        return ResponseResult.success(response);
    }
}

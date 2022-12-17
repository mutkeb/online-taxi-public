package com.mashibing.apidriver.controller;


import com.mashibing.internalcommon.dto.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {


    /**
     * 需要带token
     * @return
     */
    @GetMapping("auth")
    public String testAuth(){
        return "auth-test";
    }

    /**
     * 不需要带token
     * @return
     */
    @GetMapping("noauth")
    public String testNoAuth(){
        return "noauth-test";
    }
}

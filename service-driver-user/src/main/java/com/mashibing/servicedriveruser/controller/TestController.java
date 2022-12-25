package com.mashibing.servicedriveruser.controller;

import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.servicedriveruser.mapper.DriverUserMapper;
import com.mashibing.servicedriveruser.service.DriverUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private DriverUserService driverUserService;

    @GetMapping("/test-driver")
    public String test(){
        return "test-driver";
    }

    @GetMapping("/test-mapper")
    public ResponseResult testMapper(){
        return driverUserService.test();
    }

    //  测试mapper中得xml是否正常使用
    @Autowired
    private DriverUserMapper driverUserMapper;

    @GetMapping("/test-xml")
    public int testXml(String cityCode){
        int i = driverUserMapper.select1(cityCode);
        System.out.println(i);
        return i;
    }
}

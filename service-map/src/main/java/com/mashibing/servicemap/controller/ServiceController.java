package com.mashibing.servicemap.controller;

import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.servicemap.service.ServiceMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.soap.Addressing;

/**
 * 服务管理
 */
@RestController
@RequestMapping("/service")
public class ServiceController {

    @Autowired
    private ServiceMapService serviceMapService;

    @PostMapping("/add")
    public ResponseResult add(String name){
        return serviceMapService.add(name);
    }
}

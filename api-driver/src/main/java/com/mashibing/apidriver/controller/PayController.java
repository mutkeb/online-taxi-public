package com.mashibing.apidriver.controller;

import com.mashibing.internalcommon.dto.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pay")
public class PayController {

    @PostMapping("/push-pay-info")
    public ResponseResult pushPayInfo(@RequestParam String orderId,@RequestParam String price,@RequestParam String passengerId){
        return null;
    }
}

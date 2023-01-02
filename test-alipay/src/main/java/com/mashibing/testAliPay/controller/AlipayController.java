package com.mashibing.testAliPay.controller;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.payment.app.models.AlipayTradeAppPayResponse;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/alipay")
@ResponseBody
public class AlipayController {

    @GetMapping("/pay")
    public String pay(String subject,String outTradeNo,String totalAmount){
        AlipayTradePagePayResponse response;
        try {
            response = Factory.Payment.Page().pay(subject,outTradeNo,totalAmount,"");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return response.getBody();
    }
}

package com.mashibing.apidriver.service;

import com.mashibing.internalcommon.dto.ResponseResult;
import org.springframework.stereotype.Service;

@Service
public class PayService {

    public ResponseResult pushPayInfo(String orderId,String price,Long passengerId){
        //  封装消息

        //  推送消息

        return ResponseResult.success();
    }
}

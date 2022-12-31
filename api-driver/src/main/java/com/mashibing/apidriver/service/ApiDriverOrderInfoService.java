package com.mashibing.apidriver.service;

import com.mashibing.apidriver.remote.ServiceOrderClient;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.request.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class ApiDriverOrderInfoService {

    @Autowired
    private ServiceOrderClient serviceOrderClient;

    public ResponseResult changeStatus(OrderRequest orderRequest){
        return serviceOrderClient.changeStatus(orderRequest);
    }
}

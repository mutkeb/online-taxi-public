package com.mashibing.apidriver.service;

import com.mashibing.apidriver.remote.ServiceOrderClient;
import com.mashibing.internalcommon.constant.IdentityConstant;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.request.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class ApiDriverOrderInfoService {

    @Autowired
    private ServiceOrderClient serviceOrderClient;

    public ResponseResult changeStatus(OrderRequest orderRequest){
        return serviceOrderClient.changeStatus(orderRequest);
    }

    public ResponseResult arrivedDeparture(OrderRequest orderRequest){
        return serviceOrderClient.arrivedDeparture(orderRequest);
    }

    public ResponseResult pickUpPassenger(OrderRequest orderRequest){
        return serviceOrderClient.pickUpPassenger(orderRequest);
    }

    public ResponseResult passengerGetOff(OrderRequest orderRequest){
        return serviceOrderClient.passengerGetOff(orderRequest);
    }

    public ResponseResult cancel(Long orderId){
        return serviceOrderClient.cancel(orderId, IdentityConstant.DRIVER_IDENTITY);
    }
}

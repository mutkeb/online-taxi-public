package com.mashibing.apipassenger.remote;

import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.request.OrderRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("service-order")
public interface ServiceOrderClient {

    @RequestMapping(method = RequestMethod.POST,value = "/order/add")
    public ResponseResult addOrder(@RequestBody OrderRequest orderRequest);

    @RequestMapping(method = RequestMethod.POST,value = "/order/cancel")
    public ResponseResult cancel(@RequestParam Long orderId, @RequestParam String identity);
}

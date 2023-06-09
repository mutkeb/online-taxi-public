package com.mashibing.serviceorder.controller;


import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.request.OrderRequest;
import com.mashibing.serviceorder.service.OrderInfoService;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author mutkeb
 * @since 2022-12-22
 */
@RestController
@RequestMapping("/order")
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderService;

    @PostMapping("add")
    public ResponseResult add(@RequestBody OrderRequest orderRequest){
        return orderService.add(orderRequest);
    }

    @PostMapping("/to-pick-up-passenger")
    public ResponseResult changeStatus(@RequestBody OrderRequest orderRequest){
        return orderService.toPickUpPassenger(orderRequest);
    }

    @PostMapping("/arrived-departure")
    public ResponseResult arrivedDeparture(@RequestBody OrderRequest orderRequest){
        return orderService.arrivedDeparture(orderRequest);
    }

    @PostMapping("/pick-up-passenger")
    public ResponseResult pickUpPassenger(@RequestBody OrderRequest orderRequest){
        return orderService.pickUpPassenger(orderRequest);
    }

    @PostMapping("/passenger-getoff")
    public ResponseResult passengerGetOff(@RequestBody OrderRequest orderRequest){
        return orderService.passengerGetOff(orderRequest);
    }

    @PostMapping("/push-pay-info")
    public ResponseResult pushPayInfo(@RequestBody OrderRequest orderRequest){
        return orderService.pushPayInfo(orderRequest);
    }

    @PostMapping("/pay")
    public ResponseResult pay(@RequestBody OrderRequest orderRequest){
        return orderService.pay(orderRequest);
    }

    @PostMapping("/cancel")
    public ResponseResult cancel(@RequestParam Long orderId,@RequestParam String identity){
        return orderService.cancel(orderId,identity);
    }
}

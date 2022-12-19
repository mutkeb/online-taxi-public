package com.mashibing.apidriver.service;

import com.mashibing.apidriver.remote.ServiceDriverUserClient;
import com.mashibing.internalcommon.dto.Car;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.request.ApiDriverPointRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointService {

    @Autowired
    private ServiceDriverUserClient serviceDriverUserClient;

    public ResponseResult upload(ApiDriverPointRequest apiDriverPointRequest){
        //  获得carId
        Long carId = apiDriverPointRequest.getCarId();
        //  根据carId获得车辆
        ResponseResult<Car> carResponseResult = serviceDriverUserClient.getCarById(carId);
        Car car = carResponseResult.getData();
        String tid = car.getTid();
        String trid = car.getTrid();
        //  上传轨迹请求调用
        return ResponseResult.success();
    }
}

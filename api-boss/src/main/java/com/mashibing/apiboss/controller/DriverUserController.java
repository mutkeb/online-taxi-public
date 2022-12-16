package com.mashibing.apiboss.controller;

import com.mashibing.apiboss.service.CarService;
import com.mashibing.apiboss.service.DriverUserService;
import com.mashibing.internalcommon.dto.Car;
import com.mashibing.internalcommon.dto.DriverUser;
import com.mashibing.internalcommon.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DriverUserController {

    @Autowired
    private DriverUserService driverUserService;

    @Autowired
    private CarService carService;

    /**
     * 添加司机
     * @param driverUser
     * @return
     */
    @PostMapping("/driver-user")
    public ResponseResult addUser(@RequestBody DriverUser driverUser){
        return driverUserService.addUser(driverUser);
    }

    /**
     * 修改司机
     * @param driverUser
     * @return
     */
    @PutMapping("/driver-user")
    public ResponseResult updateUser(@RequestBody DriverUser driverUser){
        return driverUserService.updateUser(driverUser);
    }

    /**
     * 添加车辆
     * @param car
     * @return
     */
    @PostMapping("/car")
    public ResponseResult addCar(@RequestBody Car car){
        return carService.addCar(car);
    }

}

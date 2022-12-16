package com.mashibing.servicedriveruser.controller;

import com.mashibing.internalcommon.constant.DriverCarConstant;
import com.mashibing.internalcommon.dto.DriverUser;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.response.DriverUserExistsResponse;
import com.mashibing.servicedriveruser.service.DriverUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private DriverUserService driverUserService;

    /**
     * 添加司机信息
     * @param driverUser
     * @return
     */
    @PostMapping("/user")
    public ResponseResult addUser(@RequestBody DriverUser driverUser){
        log.info("service-driver-user");
        return driverUserService.addDriverUser(driverUser);
    }

    /**
     * 修改司机信息
     * @param driverUser
     * @return
     */

    @PutMapping("/user")
    public ResponseResult updateUser(@RequestBody DriverUser driverUser){
        log.info("update-driver-user");
        return driverUserService.updateUser(driverUser);
    }

    @GetMapping("/check-driver/{driverPhone}")
    public ResponseResult getUser(@PathVariable("driverPhone") String driverPhone){
        ResponseResult<DriverUser> user = driverUserService.getUser(driverPhone);
        DriverUser driverUserDb = user.getData();
        DriverUserExistsResponse response = new DriverUserExistsResponse();
        int ifExists = DriverCarConstant.DRIVER_EXISTS;
        if (driverUserDb == null){
            ifExists = DriverCarConstant.DRIVER_NOT_EXISTS;
        }else{
            response.setDriverPhone(driverUserDb.getDriverPhone());
        }
        response.setIfExists(ifExists);
        return ResponseResult.success(response);
    }


}

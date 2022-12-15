package com.mashibing.apiboss.service;


import com.mashibing.apiboss.remote.DriverUserClient;
import com.mashibing.internalcommon.dto.DriverUser;
import com.mashibing.internalcommon.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DriverUserService {

    @Autowired
    private DriverUserClient driverUserClient;

    /**
     * 添加司机
     * @param driverUser
     * @return
     */
    public ResponseResult addUser(DriverUser driverUser){
        return driverUserClient.addUser(driverUser);
    }

    /**
     * 修改司机
     * @param driverUser
     * @return
     */
    public ResponseResult updateUser(DriverUser driverUser){
        return driverUserClient.updateUser(driverUser);
    }
}

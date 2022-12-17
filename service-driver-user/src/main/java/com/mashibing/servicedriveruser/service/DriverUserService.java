package com.mashibing.servicedriveruser.service;

import com.mashibing.internalcommon.constant.CommonStatusEnum;
import com.mashibing.internalcommon.constant.DriverCarConstant;
import com.mashibing.internalcommon.dto.DriverUser;
import com.mashibing.internalcommon.dto.DriverUserWorkStatus;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.servicedriveruser.mapper.DriverUserMapper;
import com.mashibing.servicedriveruser.mapper.DriverUserWorkStatusMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class DriverUserService {

    @Autowired
    private DriverUserMapper driverUserMapper;

    @Autowired
    private DriverUserWorkStatusMapper driverUserWorkStatusMapper;

    public ResponseResult test(){
        DriverUser driverUser = driverUserMapper.selectById(1);
        log.info(driverUser.toString());
        return ResponseResult.success();
    }

    /**
     * 添加司机信息
     * @param driverUser
     * @return
     */
    public ResponseResult addDriverUser(DriverUser driverUser){
        LocalDateTime now = LocalDateTime.now();
        driverUser.setGmtCreate(now);
        driverUser.setGmtModified(now);
        driverUserMapper.insert(driverUser);

        //  初始化工作状态表
        DriverUserWorkStatus driverUserWorkStatus = new DriverUserWorkStatus();
        driverUserWorkStatus.setDriverId(driverUser.getId());
        driverUserWorkStatus.setWorkStatus(DriverCarConstant.DRIVER_WORK_STATUS_STOP);
        driverUserWorkStatus.setGmtCreate(LocalDateTime.now());
        driverUserWorkStatus.setGmtCreate(LocalDateTime.now());
        driverUserWorkStatusMapper.insert(driverUserWorkStatus);
        return ResponseResult.success();
    }

    /**
     * 修改司机信息
     * @param driverUser
     * @return
     */
    public ResponseResult updateUser(DriverUser driverUser){
        LocalDateTime now = LocalDateTime.now();
        driverUser.setGmtModified(now);
        driverUserMapper.updateById(driverUser);
        return ResponseResult.success();
    }

    public ResponseResult<DriverUser> getUser(String driverPhone){
        HashMap<String,Object> map = new HashMap();
        map.put("driver_phone",driverPhone);
        map.put("state", DriverCarConstant.DRIVER_STATE_VALID);

        List<DriverUser> driverUsers = driverUserMapper.selectByMap(map);

        if (driverUsers.isEmpty()){
            return ResponseResult.fail(CommonStatusEnum.DRIVER_NOT_EXISTS.getCode(),CommonStatusEnum.DRIVER_NOT_EXISTS.getValue());
        }
        DriverUser driverUser = driverUsers.get(0);
        return ResponseResult.success(driverUser);
    }
}

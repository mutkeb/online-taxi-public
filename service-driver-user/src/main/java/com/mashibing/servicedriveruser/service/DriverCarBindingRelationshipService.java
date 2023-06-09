package com.mashibing.servicedriveruser.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mashibing.internalcommon.constant.CommonStatusEnum;
import com.mashibing.internalcommon.constant.DriverCarConstant;
import com.mashibing.internalcommon.dto.DriverCarBindingRelationship;
import com.mashibing.internalcommon.dto.DriverUser;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.servicedriveruser.mapper.DriverCarBindingRelationshipMapper;
import com.mashibing.servicedriveruser.mapper.DriverUserMapper;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mutkeb
 * @since 2022-12-15
 */
@Service
public class DriverCarBindingRelationshipService {

    @Autowired
    private DriverCarBindingRelationshipMapper driverCarBindingRelationshipMapper;

    @Autowired
    private DriverUserMapper driverUserMapper;

    public ResponseResult bind(DriverCarBindingRelationship driverCarBindingRelationship){
        QueryWrapper<DriverCarBindingRelationship> queryWrapper = new QueryWrapper();
        queryWrapper.eq("driver_id",driverCarBindingRelationship.getDriverId());
        queryWrapper.eq("car_id",driverCarBindingRelationship.getCarId());
        queryWrapper.eq("bind_state",1);

        Integer integer = driverCarBindingRelationshipMapper.selectCount(queryWrapper);
        if (integer.intValue() > 0){
            return ResponseResult.fail(CommonStatusEnum.DRIVER_CAR_BIND_EXISTS.getCode(),CommonStatusEnum.DRIVER_CAR_BIND_EXISTS.getValue());
        }

        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("car_id",driverCarBindingRelationship.getCarId());
        queryWrapper.eq("bind_state",1);
        integer = driverCarBindingRelationshipMapper.selectCount(queryWrapper);
        if (integer.intValue() > 0){
            return ResponseResult.fail(CommonStatusEnum.CAR_BIND_EXISTS.getCode(),CommonStatusEnum.CAR_BIND_EXISTS.getValue());
        }

        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("driver_id",driverCarBindingRelationship.getDriverId());
        queryWrapper.eq("bind_state",1);
        integer = driverCarBindingRelationshipMapper.selectCount(queryWrapper);
        if (integer.intValue() > 0){
            return ResponseResult.fail(CommonStatusEnum.DRIVER_BIND_EXISTS.getCode(),CommonStatusEnum.DRIVER_BIND_EXISTS.getValue());
        }


        LocalDateTime now = LocalDateTime.now();
        driverCarBindingRelationship.setBindingTime(now);
        driverCarBindingRelationship.setBindState(DriverCarConstant.DRIVER_CAR_BIND);
        driverCarBindingRelationshipMapper.insert(driverCarBindingRelationship);
        return ResponseResult.success();
    }

    public ResponseResult unbind(DriverCarBindingRelationship driverCarBindingRelationship){
        HashMap<String,Object> map = new HashMap<>();
        map.put("driver_id",driverCarBindingRelationship.getDriverId());
        map.put("car_id",driverCarBindingRelationship.getCarId());
        map.put("bind_state",DriverCarConstant.DRIVER_CAR_BIND);

        List<DriverCarBindingRelationship> driverCarBindingRelationships = driverCarBindingRelationshipMapper.selectByMap(map);
        if (driverCarBindingRelationships.isEmpty()){
            return ResponseResult.fail(CommonStatusEnum.DRIVER_CAR_BIND_NOT_EXISTS.getCode(),CommonStatusEnum.DRIVER_CAR_BIND_NOT_EXISTS.getValue());
        }
        DriverCarBindingRelationship relationship = driverCarBindingRelationships.get(0);
        LocalDateTime now = LocalDateTime.now();
        relationship.setBindingTime(now);
        relationship.setBindState(DriverCarConstant.DRIVER_CAR_UNBIND);
        driverCarBindingRelationshipMapper.updateById(relationship);
        return ResponseResult.success();
    }

    public ResponseResult getDriveCarBindingRelationshipByDriverPhone(String driverPhone){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("driver_phone",driverPhone);

        DriverUser driverUser = driverUserMapper.selectOne(queryWrapper);
        Long driverId = driverUser.getId();

        QueryWrapper<DriverCarBindingRelationship> driverCarBindingRelationshipQueryWrapper = new QueryWrapper<>();
        driverCarBindingRelationshipQueryWrapper.eq("driver_id",driverId);
        driverCarBindingRelationshipQueryWrapper.eq("bind_state",DriverCarConstant.DRIVER_CAR_BIND);

        DriverCarBindingRelationship driverCarBindingRelationship = driverCarBindingRelationshipMapper.selectOne(driverCarBindingRelationshipQueryWrapper);
        return ResponseResult.success(driverCarBindingRelationship);

    }
}

package com.mashibing.apiboss.service;

import com.mashibing.apiboss.remote.ServiceDriverUserClient;
import com.mashibing.internalcommon.dto.DriverCarBindingRelationship;
import com.mashibing.internalcommon.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DriverCarRelationshipService {

    @Autowired
    private ServiceDriverUserClient serviceDriverUserClient;

    /**
     * 司机车辆关系绑定
     * @param driverCarBindingRelationship
     * @return
     */
    public ResponseResult bind(DriverCarBindingRelationship driverCarBindingRelationship){
        return serviceDriverUserClient.bind(driverCarBindingRelationship);
    }

    /**
     * 司机车辆关系解绑
     * @param driverCarBindingRelationship
     * @return
     */
    public ResponseResult unbind(DriverCarBindingRelationship driverCarBindingRelationship){
        return serviceDriverUserClient.unbind(driverCarBindingRelationship);
    }
}

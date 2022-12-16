package com.mashibing.apiboss.controller;

import com.mashibing.apiboss.service.DriverCarRelationshipService;
import com.mashibing.internalcommon.dto.DriverCarBindingRelationship;
import com.mashibing.internalcommon.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DriverCarRelationshipController {

    @Autowired
    private DriverCarRelationshipService driverCarRelationshipService;

    @PostMapping("/driver-car-binding-relationship/bind")
    public ResponseResult bind(@RequestBody DriverCarBindingRelationship driverCarBindingRelationship){
        return driverCarRelationshipService.bind(driverCarBindingRelationship);
    }

    @PostMapping("/driver-car-binding-relationship/unbind")
    public ResponseResult unbind(@RequestBody DriverCarBindingRelationship driverCarBindingRelationship){
        return driverCarRelationshipService.unbind(driverCarBindingRelationship);
    }
}

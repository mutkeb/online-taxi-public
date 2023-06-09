package com.mashibing.apidriver.remote;

import com.mashibing.internalcommon.dto.*;
import com.mashibing.internalcommon.response.DriverUserExistsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("service-driver-user")
public interface ServiceDriverUserClient {

    @RequestMapping(method = RequestMethod.PUT,value = "/user")
    public ResponseResult updateUser(@RequestBody DriverUser driverUser);

    @RequestMapping(method = RequestMethod.GET,value = "/check-driver/{driverPhone}")
    public ResponseResult<DriverUserExistsResponse> getUser(@PathVariable("driverPhone")String driverPhone);

    @RequestMapping(method = RequestMethod.GET,value = "/car")
    public ResponseResult<Car> getCarById(@RequestParam long id);

    @RequestMapping(method = RequestMethod.POST,value = "/driver-user-work-status")
    public ResponseResult changeWorkStatus(@RequestBody DriverUserWorkStatus driverUserWorkStatus);

    @RequestMapping(method = RequestMethod.GET,value = "/driver-car-binding-relationship")
    public ResponseResult<DriverCarBindingRelationship> getDriveCarBindingRelationshipByDriverPhone(@RequestParam String driverPhone);
}

package com.mashibing.internalcommon.response;

import lombok.Data;

@Data
public class OrderDriverResponse {

    private Long carId;

    private String driverPhone;

    private Long driverId;

    private String licenseId;

    private String vehicleNo;

    private String vehicleType;
}

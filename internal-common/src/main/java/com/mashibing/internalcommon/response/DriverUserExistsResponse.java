package com.mashibing.internalcommon.response;

import lombok.Data;

@Data
public class DriverUserExistsResponse {

    private int ifExists;

    private String driverPhone;
}

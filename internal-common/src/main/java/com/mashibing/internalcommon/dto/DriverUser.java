package com.mashibing.internalcommon.dto;

import lombok.Data;

import java.util.Date;

@Data
public class DriverUser {
    private Integer id;
    private String address;
    private String driverName;
    private String driverPhone;
    private String driverGender;
    private Date driverBirthday;
    private String  driverNation;
    private String driverContactAddress;
    private String licenseId;
    private Date getDriverLicenseDate;
    private Date driverLicenseOn;
    private Date driverLicenseOff;
    private Integer taxiDriver;
    private String networkCarIssueOrganization;
    private String certificateNo;
    private Date networkCarIssueDate;
    private Date getNetworkCarProofDate;
    private Date NetworkCarProofOn;
    private Date networkCarProofOff;
    private Date registerDate;
    private Integer commercialType;
    private String contractCompany;
    private Date contractOn;
    private Date contractOff;
    private Integer state;
    private Date gmtCreate;
    private Date gmtModified;

}

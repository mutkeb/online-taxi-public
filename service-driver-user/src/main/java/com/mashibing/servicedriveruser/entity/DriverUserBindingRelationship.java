package com.mashibing.servicedriveruser.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author mutkeb
 * @since 2022-12-15
 */
@TableName("driver_user_binding_relationship")
@Data
public class DriverUserBindingRelationship implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String address;

    private String driverName;

    private String driverPhone;

    private Integer driverGender;

    private LocalDate driverBirthday;

    private String driverNation;

    private String driverContactAddress;

    private String licenseId;

    private LocalDate getDriverLicenseDate;

    private LocalDate driverLicenseOn;

    private LocalDate driverLicenseOff;

    private Integer taxiDriver;

    private String networkCarIssueOrganization;

    private String certificateNo;

    private LocalDate networkCarIssueDate;

    private LocalDate getNetworkCarProofDate;

    private LocalDate networkCarProofOn;

    private LocalDate networkCarProofOff;

    private LocalDate registerDate;

    private Integer commercialType;

    private String contractCompany;

    private LocalDate contractOn;

    private LocalDate contractOff;

    private Integer state;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;


}

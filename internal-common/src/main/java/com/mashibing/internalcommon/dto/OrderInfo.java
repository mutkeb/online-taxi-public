package com.mashibing.internalcommon.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author mutkeb
 * @since 2022-12-22
 */
@Data
public class OrderInfo implements Serializable {


    private Long id;

    private Long passengerId;

    private String passengerPhone;

    private Long driverId;

    private String driverPhone;

    private Long carId;

    private String vehicleType;

    private String address;

    private LocalDateTime orderTime;

    private LocalDateTime departTime;

    private String departure;

    private String depLongitude;

    private String depLatitude;

    private String destination;

    private String destLongitude;

    private String destLatitude;

    private Integer encrypt;

    private String fareType;

    private Integer fareVersion;

    private String receiveOrderCarLongitude;

    private String receiveOrderCarLatitude;

    private LocalDateTime receiveOrderTime;

    private String licenseId;

    private String vehicleNo;

    private LocalDateTime toPickUpPassengerTime;

    private String toPickUpPassengerLongitude;

    private String toPickUpPassengerLatitude;

    private String toPickUpPassengerAddress;

    private LocalDateTime driverArrivedDepatureTime;

    private LocalDateTime pickUpPassengerTime;

    private String pickUpPassengerLongitude;

    private String pickUpPassengerLatitude;

    private LocalDateTime passengerGetoffTime;

    private String passengerGetoffLongitude;

    private String passengerGetoffLatitude;

    private LocalDateTime cancelTime;

    private Integer cancelOperator;

    private Integer cancelTypeCode;

    private Long driveMile;

    private Long driveTime;

    private Integer orderStatus;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;


}

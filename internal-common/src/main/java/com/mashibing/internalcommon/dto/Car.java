package com.mashibing.internalcommon.dto;

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
public class Car implements Serializable {


    private Long id;

    private String address;

    private String vehicleNo;

    private String plateColor;

    private Integer seats;

    private String brand;

    private String model;

    private String vehicle;

    private String ownerName;

    private String vehicleColor;

    private String engineId;

    private String vin;

    private LocalDate certifyDateA;

    private String fueType;

    private String engineDisplace;

    private String transAgency;

    private String transArea;

    private LocalDate transDateStart;

    private LocalDate transDateEnd;

    private LocalDate certifyDateB;

    private String fixState;

    private LocalDate nextFixDate;

    private String checkState;

    private String feePrintId;

    private String gpsBrand;

    private String gpsModel;

    private LocalDate gpsInstallDate;

    private LocalDate registerDate;

    private Integer commercialDate;

    private String fareType;

    private Boolean state;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

}

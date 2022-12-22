package com.mashibing.internalcommon.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author mutkeb
 * @since 2022-12-22
 */
@Data
public class PriceRule implements Serializable {


    private String cityCode;

    private String vehicleType;

    private Double startFare;

    private Integer startMile;

    private Double unitPricePerMile;

    private Double unitPricePerMinute;

    private String fareType;

    private Integer fareVersion;


}

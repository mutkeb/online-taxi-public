package com.mashibing.internalcommon.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderRequest {

    //  乘客Id
    private Long passengerId;
    //  乘客电话
    private String passengerPhone;
    //  下单行政区域
    private String address;
    //  出发时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dapartTIme;
    //  下单时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;
    //  出发地址
    private String departure;
    //  出发经度
    private String depLongitude;
    //  出发纬度
    private String depLatitude;
    //  目的地地址
    private String destination;
    //  目的地经度
    private String destLongitude;
    //  目的地纬度
    private String destLatitude;
    //  坐标加密标识  1:gcj-02,2:wgs84,3:bd-09,4:cgcs2000北斗,0:其他
    private Integer encrypt;
    //  运价类型编码
    private String fareType;
    //  运价版本
    private Integer fareVersion;
}

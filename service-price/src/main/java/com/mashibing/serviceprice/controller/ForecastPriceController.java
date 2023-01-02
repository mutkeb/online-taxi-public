package com.mashibing.serviceprice.controller;


import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.request.ForecastPriceDTO;
import com.mashibing.serviceprice.service.ForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ForecastPriceController {

    @Autowired
    private ForecastService forecastService;

    /**
     * 计算预估价格
     * @param forecastPriceDTO
     * @return
     */
    @PostMapping("/forecast-price")
    public ResponseResult forecastPrice(@RequestBody ForecastPriceDTO forecastPriceDTO){
        String destLatitude = forecastPriceDTO.getDestLatitude();
        String destLongitude = forecastPriceDTO.getDestLongitude();
        String depLatitude = forecastPriceDTO.getDepLatitude();
        String depLongitude = forecastPriceDTO.getDepLongitude();
        String cityCode = forecastPriceDTO.getCityCode();
        String vehicleTpye = forecastPriceDTO.getVehicleType();

        return forecastService.forecastPrice(depLongitude,depLatitude,destLongitude,destLatitude,cityCode,vehicleTpye);
    }

    /**
     * 计算实际价格
     * @param distance
     * @param duration
     * @param cityCode
     * @param vehicleType
     * @return
     */
    @PostMapping("/calculate-price")
    public ResponseResult calculatePrice(@RequestParam Integer distance,@RequestParam Integer duration,@RequestParam String cityCode,@RequestParam String vehicleType){
        return forecastService.calculatePrice(distance, duration, cityCode, vehicleType);
    }
}

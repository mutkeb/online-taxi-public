package com.mashibing.apipassenger.service;


import com.mashibing.apipassenger.remote.ServicePriceClient;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.request.ForecastPriceDTO;
import com.mashibing.internalcommon.response.ForecastPriceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ForecastPriceService {


    @Autowired
    private ServicePriceClient servicePriceClient;
    /**
     * 根据出发地经纬度和目的地经纬度预估价格
     * @param depLongitude
     * @param depLatitude
     * @param destLongitude
     * @param destLatitude
     * @return
     */
    public ResponseResult forecastPrice(String depLongitude,String depLatitude,String destLongitude,String destLatitude,
                                        String cityCode,String vehicleType){
        log.info("出发地经度：" + depLongitude);
        log.info("出发地纬度：" + depLatitude);
        log.info("目的地经度：" + destLongitude);
        log.info("目的地纬度：" + destLatitude);

        log.info("调用计价服务，计算价格");
        ForecastPriceDTO priceDTO = new ForecastPriceDTO();
        priceDTO.setDepLongitude(depLongitude);
        priceDTO.setDepLatitude(depLatitude);
        priceDTO.setDestLongitude(destLongitude);
        priceDTO.setDestLatitude(destLatitude);
        priceDTO.setCityCode(cityCode);
        priceDTO.setVehicleType(vehicleType);
        ResponseResult<ForecastPriceResponse> forecast = servicePriceClient.forecast(priceDTO);

        return ResponseResult.success(forecast.getData());
    }

}

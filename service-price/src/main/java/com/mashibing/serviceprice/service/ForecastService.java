package com.mashibing.serviceprice.service;

import com.mashibing.internalcommon.constant.CommonStatusEnum;
import com.mashibing.internalcommon.dto.PriceRule;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.request.ForecastPriceDTO;
import com.mashibing.internalcommon.response.DirectionResponse;
import com.mashibing.internalcommon.response.ForecastPriceResponse;
import com.mashibing.serviceprice.mapper.PriceRuleMapper;
import com.mashibing.serviceprice.remote.ServiceMapClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ForecastService {

    @Autowired
    private ServiceMapClient serviceMapClient;

    @Autowired
    private PriceRuleMapper priceRuleMapper;

    public ResponseResult forecastPrice(String depLongitude,String depLatitude,String destLongitude,String destLatitude){
        log.info("出发地经度：" + depLongitude);
        log.info("出发地纬度：" + depLatitude);
        log.info("目的地经度：" + destLongitude);
        log.info("目的地纬度：" + destLatitude);

        log.info("调用地图服务，查询距离和时长");
        ForecastPriceDTO forecastPriceDTO = new ForecastPriceDTO();
        forecastPriceDTO.setDepLongitude(depLongitude);
        forecastPriceDTO.setDepLatitude(depLatitude);
        forecastPriceDTO.setDestLongitude(destLongitude);
        forecastPriceDTO.setDestLatitude(destLatitude);
        ResponseResult<DirectionResponse> direction = serviceMapClient.direction(forecastPriceDTO);
        Integer distance = direction.getData().getDistance();
        Integer duration = direction.getData().getDuration();
        log.info("距离是:" + distance + ",时长：" + duration);

        log.info("读取计价规则");
        Map<String,Object> queryMap = new HashMap();
        queryMap.put("city_code","110000");
        queryMap.put("vehicle_type","1");
        List priceRules = priceRuleMapper.selectByMap(queryMap);
        if (priceRules.size() == 0){
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_EXISTS.getCode(),CommonStatusEnum.PRICE_RULE_EXISTS.getValue());
        }
        PriceRule priceRule = (PriceRule)priceRules.get(0);
        log.info("根据距离、时长、计价规则计算价格");


        double price = getPrice(distance, duration, priceRule);

        ForecastPriceResponse response = new ForecastPriceResponse();
        response.setPrice(price);
        return ResponseResult.success(response);
    }

    /**
     * 根据距离、时长和计算规则计算最终价格
     * @param distance
     * @param duration
     * @param priceRule
     * @return
     */
    private static double getPrice(Integer distance,Integer duration,PriceRule priceRule){
        //  BigDecimal
        BigDecimal price = new BigDecimal(0);
        //  起步价
        Double startFare = priceRule.getStartFare();
        BigDecimal startFareDecimal = new BigDecimal(startFare);
        price = price.add(startFareDecimal);
        //  里程费
        //  总里程m
        BigDecimal distanceDecimal = new BigDecimal(distance);
        //  总里程km
        BigDecimal distanceMileDecimal = distanceDecimal.divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP);
        //  起步里程
        Integer startMile = priceRule.getStartMile();
        BigDecimal startMileDecimal = new BigDecimal(startMile);
        double distanceSubtract = distanceMileDecimal.subtract(startMileDecimal).doubleValue();
        //  对于小于起始里程的当作起始里程处理
        double mile = distanceSubtract < 0 ? 0 : distanceSubtract;
        BigDecimal mileDecimal = new BigDecimal(mile);
        //  计乘单价    元/km
        Double unitPricePerMile = priceRule.getUnitPricePerMile();
        BigDecimal unitPricePerMileDecimal = new BigDecimal(unitPricePerMile);

        BigDecimal mileFare = mileDecimal.multiply(unitPricePerMileDecimal).setScale(2, BigDecimal.ROUND_HALF_UP);
        price = price.add(mileFare);

        //  时长费
        BigDecimal time = new BigDecimal(duration);
        //   时长的分钟数
        BigDecimal timeDecimal = time.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP);
        //  计时单价
        Double unitPricePerMinute = priceRule.getUnitPricePerMinute();
        BigDecimal unitPricePerMinuteDecimal = new BigDecimal(unitPricePerMinute);
        //  时长费用
        BigDecimal timeFare = timeDecimal.multiply(unitPricePerMinuteDecimal);
        price = price.add(timeFare);

        return price.doubleValue();
    }

    public static void main(String[] args) {
        PriceRule priceRule = new PriceRule();
        priceRule.setUnitPricePerMile(1.8);
        priceRule.setUnitPricePerMinute(0.5);
        priceRule.setStartFare(10.0);
        priceRule.setStartMile(3);
        System.out.println(getPrice(6500,1800,priceRule));
    }
}

package com.mashibing.serviceprice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mashibing.internalcommon.constant.CommonStatusEnum;
import com.mashibing.internalcommon.dto.PriceRule;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mashibing.serviceprice.mapper.PriceRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author mutkeb
 * @since 2022-12-22
 */
@Service
public class PriceRuleService {

    @Autowired
    private PriceRuleMapper priceRuleMapper;

    public ResponseResult add(PriceRule priceRule) {
        //  拼接fare_type
        String cityCode = priceRule.getCityCode();
        String vehicleType = priceRule.getVehicleType();
        String fareType = cityCode + "$" + vehicleType;

        priceRule.setFareType(fareType);

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("city_code", cityCode);
        queryWrapper.eq("vehicle_type", vehicleType);
        queryWrapper.orderByDesc("fare_version");

        List<PriceRule> list = priceRuleMapper.selectList(queryWrapper);
        Integer fareVersion = 0;
        if (list.size() > 0) {
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_EXISTS.getCode(), CommonStatusEnum.PRICE_RULE_EXISTS.getValue());
        }
        priceRule.setFareVersion(++fareVersion);
        priceRuleMapper.insert(priceRule);
        return ResponseResult.success();
    }

    public ResponseResult edit(PriceRule priceRule) {
        //  拼接fare_type
        String cityCode = priceRule.getCityCode();
        String vehicleType = priceRule.getVehicleType();
        String fareType = cityCode + "$" + vehicleType;

        priceRule.setFareType(fareType);

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("city_code", cityCode);
        queryWrapper.eq("vehicle_type", vehicleType);
        queryWrapper.orderByDesc("fare_version");

        List<PriceRule> list = priceRuleMapper.selectList(queryWrapper);
        Integer fareVersion = 0;
        if (list.size() > 0) {
            PriceRule lastPriceRule = list.get(0);
            Double unitPricePerMinute = lastPriceRule.getUnitPricePerMinute();
            Double unitPricePerMile = lastPriceRule.getUnitPricePerMile();
            Integer startMile = lastPriceRule.getStartMile();
            Double startFare = lastPriceRule.getStartFare();
            if (unitPricePerMinute.doubleValue() == priceRule.getUnitPricePerMinute().doubleValue()
                    && unitPricePerMile.doubleValue() == priceRule.getUnitPricePerMile().doubleValue()
                    && startMile.intValue() == priceRule.getStartMile().intValue()
                    && startFare.doubleValue() == priceRule.getStartFare().doubleValue()) {
                return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_NOT_EDIT.getCode(),CommonStatusEnum.PRICE_RULE_NOT_EDIT.getValue());
            }
            fareVersion = lastPriceRule.getFareVersion();
        }
        priceRule.setFareVersion(++fareVersion);
        priceRuleMapper.insert(priceRule);
        return ResponseResult.success();
    }

}

package com.mashibing.serviceprice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mashibing.internalcommon.constant.CommonStatusEnum;
import com.mashibing.internalcommon.dto.PriceRule;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mashibing.serviceprice.mapper.PriceRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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

    public ResponseResult<PriceRule> getNewestVersion(String fareType){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("fare_type",fareType);
        queryWrapper.orderByDesc("fare_version");

        List<PriceRule> list = priceRuleMapper.selectList(queryWrapper);
        if (list.size() > 0){
            return ResponseResult.success(list.get(0));
        }else{
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_EMPTY.getCode(),CommonStatusEnum.PRICE_RULE_EMPTY.getValue());
        }
    }

    public ResponseResult<Boolean> isNew(@RequestParam String fareType,@RequestParam Integer fareVersion){
        ResponseResult<PriceRule> newestVersion = getNewestVersion(fareType);
        if (newestVersion.getCode() == CommonStatusEnum.PRICE_RULE_EMPTY.getCode()){
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_EMPTY.getCode(),CommonStatusEnum.PRICE_RULE_EMPTY.getValue());
        }
        PriceRule priceRule = newestVersion.getData();
        Integer fareVersionDB = priceRule.getFareVersion();
        if (fareVersion < fareVersionDB){
            return ResponseResult.success(false);
        }else {
            return ResponseResult.success(true);
        }
    }

    public ResponseResult<Boolean> ifExists(@RequestBody PriceRule priceRule){
        QueryWrapper queryWrapper = new QueryWrapper();
        String cityCode = priceRule.getCityCode();
        String vehicleType = priceRule.getVehicleType();
        queryWrapper.eq("city_code",cityCode);
        queryWrapper.eq("vehicle_type",vehicleType);
        queryWrapper.orderByDesc("fare_version");

        List list = priceRuleMapper.selectList(queryWrapper);
        if (list.size() > 0){
            return ResponseResult.success(true);
        }else{
            return ResponseResult.success(false);
        }
    }
}

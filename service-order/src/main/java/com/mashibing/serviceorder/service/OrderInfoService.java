package com.mashibing.serviceorder.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mashibing.internalcommon.constant.CommonStatusEnum;
import com.mashibing.internalcommon.constant.OrderConstant;
import com.mashibing.internalcommon.dto.OrderInfo;
import com.mashibing.internalcommon.dto.PriceRule;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.request.OrderRequest;
import com.mashibing.internalcommon.response.TerminalResponse;
import com.mashibing.internalcommon.util.RedisPrefixUtils;
import com.mashibing.serviceorder.mapper.OrderInfoMapper;
import com.mashibing.serviceorder.remote.ServiceDriverUserClient;
import com.mashibing.serviceorder.remote.ServiceMapClient;
import com.mashibing.serviceorder.remote.ServicePriceClient;
import jdk.nashorn.internal.ir.Terminal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mutkeb
 * @since 2022-12-22
 */
@Service
@Slf4j
public class OrderInfoService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private ServicePriceClient servicePriceClient;

    @Autowired
    private ServiceDriverUserClient serviceDriverUserClient;

    @Autowired
    private ServiceMapClient serviceMapClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    public ResponseResult add(OrderRequest orderRequest){
        //  查看当前城市是否有可用司机
        ResponseResult<Boolean> availableDriver = serviceDriverUserClient.isAvailableDriver(orderRequest.getAddress());
        if (!availableDriver.getData()){
            return ResponseResult.fail(CommonStatusEnum.CITY_DRIVER_EMPTY.getCode(),CommonStatusEnum.CITY_DRIVER_EMPTY.getValue());
        }

        //  判断下单城市和计价规则是否存在
        if (!isPriceRuleExists(orderRequest)){
            return ResponseResult.fail(CommonStatusEnum.CITY_SERVICE_NOT_SERVICE.getCode(),CommonStatusEnum.CITY_SERVICE_NOT_SERVICE.getValue());
        }
        //  需要判断计价规则的版本是否最新
        String fareType = orderRequest.getFareType();
        Integer fareVersion = orderRequest.getFareVersion();
        ResponseResult<Boolean> aNew = servicePriceClient.isNew(fareType, fareVersion);
        if (!(aNew.getData())){
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_CHANGED.getCode(),CommonStatusEnum.PRICE_RULE_CHANGED.getValue());
        }
        //  判断下单设备是否在黑名单中
        if (isBlackDevice(orderRequest)) {
            return ResponseResult.fail(CommonStatusEnum.DEVICE_IS_BLACK.getCode(), CommonStatusEnum.DEVICE_IS_BLACK.getValue());
        }

        //  判断有无正在运行的订单不允许下单
        if (isOrderGoingOn(orderRequest.getPassengerId()) > 0){
            return ResponseResult.fail(CommonStatusEnum.ORDER_GOING_ON.getCode(),CommonStatusEnum.ORDER_GOING_ON.getValue());
        }

        //  创建订单
        OrderInfo order = new OrderInfo();

        BeanUtils.copyProperties(orderRequest,order);
        order.setOrderStatus(OrderConstant.ORDER_START);

        LocalDateTime now = LocalDateTime.now();
        order.setGmtCreate(now);
        order.setGmtModified(now);

        orderInfoMapper.insert(order);
        //  派单
        dispatchRealTimeOrder(order);
        return ResponseResult.success();
    }

    private boolean isPriceRuleExists(OrderRequest orderRequest){
        String fareType = orderRequest.getFareType();
        int index = fareType.indexOf("$");
        String cityCode = fareType.substring(0, index);
        String vehicleType = fareType.substring(index + 1);

        PriceRule priceRule = new PriceRule();
        priceRule.setCityCode(cityCode);
        priceRule.setVehicleType(vehicleType);
        ResponseResult<Boolean> booleanResponseResult = servicePriceClient.ifExists(priceRule);

        return booleanResponseResult.getData();
    }

    private boolean isBlackDevice(OrderRequest orderRequest){
        String deviceCode = orderRequest.getDeviceCode();
        //  生成key
        String deviceCodeKey = RedisPrefixUtils.blackDeviceCodePrefix + deviceCode;
        Boolean aBoolean = stringRedisTemplate.hasKey(deviceCodeKey);
        if (aBoolean){
            String s = stringRedisTemplate.opsForValue().get(deviceCodeKey);
            int i = Integer.parseInt(s);
            if (i > 1){
                return true;
            }
            stringRedisTemplate.opsForValue().increment(deviceCodeKey);
        }else{
            stringRedisTemplate.opsForValue().setIfAbsent(deviceCodeKey,"1", 1L,TimeUnit.HOURS);
        }
        return false;
    }

    private Integer isOrderGoingOn(Long passengerId){
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper();
        queryWrapper.eq("passenger_id",passengerId);
        queryWrapper.and(wrapper->wrapper.eq("order_status",OrderConstant.ORDER_START)
                .or().eq("order_status",OrderConstant.DRIVER_RECEIVE_ORDER)
                .or().eq("order_status",OrderConstant.DRIVER_TO_PICK_UP_PASSENGER)
                .or().eq("order_status",OrderConstant.DRIVER_ARRIVED_DEPARTURE)
                .or().eq("order_status",OrderConstant.PICK_UP_PASSENGER)
                .or().eq("order_status",OrderConstant.PASSENGER_GET_OFF)
                .or().eq("order_status",OrderConstant.TO_START_PAY)
        );
        Integer integer = orderInfoMapper.selectCount(queryWrapper);
        return integer;
    }

    /**
     * 实时订单派单逻辑
     * @param orderInfo
     */
    public void dispatchRealTimeOrder(OrderInfo orderInfo){
        String depLongitude = orderInfo.getDepLongitude();
        String depLatitude = orderInfo.getDepLatitude();
        String center = depLatitude + "," + depLongitude;
        List<Long> radiusList = new ArrayList<>();
        radiusList.add(2000L);
        radiusList.add(4000L);
        radiusList.add(5000L);
        ResponseResult<List<TerminalResponse>> listResponseResult = null;
        for (int i = 0; i < radiusList.size(); i++) {
            Long radius = radiusList.get(i);
            listResponseResult = serviceMapClient.aroundSearch(center, radius);
            log.info("在半径为：" + radius +"的范围内寻找车辆");
            //  获得终端

            //  解析终端

            //  根据解析出来的终端，查询车辆信息

            //  获得符合的车辆，进行派单

            //  如果派单成功则跳出循环
        }

    }
}

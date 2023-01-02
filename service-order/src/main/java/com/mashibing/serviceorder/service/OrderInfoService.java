package com.mashibing.serviceorder.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mashibing.internalcommon.constant.CommonStatusEnum;
import com.mashibing.internalcommon.constant.IdentityConstant;
import com.mashibing.internalcommon.constant.OrderConstant;
import com.mashibing.internalcommon.dto.Car;
import com.mashibing.internalcommon.dto.OrderInfo;
import com.mashibing.internalcommon.dto.PriceRule;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.request.OrderRequest;
import com.mashibing.internalcommon.request.PriceRuleIsNewRequest;
import com.mashibing.internalcommon.response.OrderDriverResponse;
import com.mashibing.internalcommon.response.TerminalResponse;
import com.mashibing.internalcommon.response.TrsearchResponse;
import com.mashibing.internalcommon.util.RedisPrefixUtils;
import com.mashibing.serviceorder.mapper.OrderInfoMapper;
import com.mashibing.serviceorder.remote.ServiceDriverUserClient;
import com.mashibing.serviceorder.remote.ServiceMapClient;
import com.mashibing.serviceorder.remote.ServicePriceClient;
import com.mashibing.serviceorder.remote.ServiceSsePushClient;
import jdk.nashorn.internal.ir.Terminal;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    private RedissonClient redissonClient;

    @Autowired
    private ServiceSsePushClient serviceSsePushClient;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 添加订单
     * @param orderRequest
     * @return
     */
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
        PriceRuleIsNewRequest priceRuleIsNewRequest = new PriceRuleIsNewRequest();
        priceRuleIsNewRequest.setFareType(fareType);
        priceRuleIsNewRequest.setFareVersion(fareVersion);
        ResponseResult<Boolean> aNew = servicePriceClient.isNew(priceRuleIsNewRequest);
        if (!(aNew.getData())){
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_CHANGED.getCode(),CommonStatusEnum.PRICE_RULE_CHANGED.getValue());
        }
        //  判断下单设备是否在黑名单中
        if (isBlackDevice(orderRequest)) {
            return ResponseResult.fail(CommonStatusEnum.DEVICE_IS_BLACK.getCode(), CommonStatusEnum.DEVICE_IS_BLACK.getValue());
        }

        //  判断有无正在运行的订单不允许下单
        if (isPassengerOrderGoingOn(orderRequest.getPassengerId()) > 0){
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
        //  定时任务的处理
        for (int i = 0; i < 6; i++) {
            //  派单
            int result = dispatchRealTimeOrder(order);
            if (result == 1){
                break;
            }

            //  等待20秒
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return ResponseResult.success();
    }

    /**
     * 判断计价规则是否存在
     * @param orderRequest
     * @return
     */
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

    /**
     * 判断是否是黑名单设备
     * @param orderRequest
     * @return
     */
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

    /**
     * 判断乘客是否有正在运行的订单
     * @param passengerId
     * @return
     */
    private Integer isPassengerOrderGoingOn(Long passengerId){
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
     * 查看司机是否有正在运行的订单
     * @param driverId
     * @return
     */
    private Integer isDriverOrderGoingOn(Long driverId){
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper();
        queryWrapper.eq("driver_id",driverId);
        queryWrapper.and(wrapper->wrapper.eq("order_status",OrderConstant.ORDER_START)
                .or().eq("order_status",OrderConstant.DRIVER_RECEIVE_ORDER)
                .or().eq("order_status",OrderConstant.DRIVER_TO_PICK_UP_PASSENGER)
                .or().eq("order_status",OrderConstant.DRIVER_ARRIVED_DEPARTURE)
                .or().eq("order_status",OrderConstant.PICK_UP_PASSENGER)
        );
        Integer integer = orderInfoMapper.selectCount(queryWrapper);
        return integer;
    }

    /**
     * 实时订单派单逻辑
     * @param orderInfo
     */
    public int dispatchRealTimeOrder(OrderInfo orderInfo){
        log.info("循环一次");
        int result = 0;

        String depLongitude = orderInfo.getDepLongitude();
        String depLatitude = orderInfo.getDepLatitude();
        String center = depLatitude + "," + depLongitude;
        List<Long> radiusList = new ArrayList<>();
        radiusList.add(2000L);
        radiusList.add(4000L);
        radiusList.add(5000L);
        ResponseResult<List<TerminalResponse>> listResponseResult = null;
        boolean ifFind = false;
        for (int i = 0; i < radiusList.size() && !ifFind; i++) {
            Long radius = radiusList.get(i);
            listResponseResult = serviceMapClient.aroundSearch(center, radius);
            log.info("在半径为：" + radius +"的范围内寻找车辆,结果:" + JSONArray.fromObject(listResponseResult.getData()).getJSONObject(0).toString());
            //  获得终端 {"carId":1604743372085096449,"tid":"612821667"}

            //  解析终端
            List<TerminalResponse> data = listResponseResult.getData();
            for (int j = 0; j < data.size(); j++) {
                TerminalResponse terminalResponse = data.get(j);
                String latitude = terminalResponse.getLatitude();
                String longitude = terminalResponse.getLongitude();
                long carId = terminalResponse.getCarId();

                ResponseResult<OrderDriverResponse> availableDriver = serviceDriverUserClient.getAvailableDriver(carId);
                if (availableDriver.getCode() == CommonStatusEnum.AVAILABLE_DRIVER_EMPTY.getCode()){
                    log.info("没有车辆ID：" + carId + "对应的司机");
                }else{
                    log.info("车辆ID:" + carId +",找到了正在出车的司机");
                    //  查看司机是否有正在运行的订单
                    OrderDriverResponse orderDriverResponse = availableDriver.getData();
                    Long driverId = orderDriverResponse.getDriverId();
                    String licenseId = orderDriverResponse.getLicenseId();
                    String vehicleNo = orderDriverResponse.getVehicleNo();
                    String driverPhone = orderDriverResponse.getDriverPhone();
                    //  判断车辆的车型是否符合(搜索到的车辆不一定都是符合要求的)
                    String vehicleTypeFromCar = orderDriverResponse.getVehicleType();
                    String vehicleType = orderInfo.getVehicleType();
                    if (!vehicleTypeFromCar.trim().equals(vehicleType.trim())){
                        log.info("车型不符合");
                        continue;
                    }

                    String lockKey = (driverId + "").intern();
                    RLock lock = redissonClient.getLock(lockKey);
                    lock.lock();

                    Integer driverOrderGoingOn = isDriverOrderGoingOn(driverId);
                    if (driverOrderGoingOn > 0){
                        log.info("司机Id:" + driverId + "，正在进行的订单数量:" + driverOrderGoingOn);
                        lock.unlock();
                        continue;
                    }
                    //  订单直接匹配司机
                    //  查询当前车辆信息

                    //  查询当前司机信息
                    orderInfo.setDriverId(driverId);
                    orderInfo.setDriverPhone(driverPhone);
                    orderInfo.setCarId(carId);
                    orderInfo.setLicenseId(licenseId);
                    orderInfo.setVehicleNo(vehicleNo);

                    orderInfo.setReceiveOrderCarLatitude(latitude);
                    orderInfo.setReceiveOrderCarLongitude(longitude);
                    orderInfo.setReceiveOrderTime(LocalDateTime.now());

                    orderInfo.setOrderStatus(OrderConstant.DRIVER_RECEIVE_ORDER);

                    orderInfoMapper.updateById(orderInfo);
                    ifFind = true;

                    //  通知司机
                    JSONObject driverContent = new JSONObject();
                    driverContent.put("passengerId",orderInfo.getPassengerId());
                    driverContent.put("passengerPhone",orderInfo.getPassengerPhone());
                    driverContent.put("departure",orderInfo.getDeparture());
                    driverContent.put("depLongitude",orderInfo.getDepLongitude());
                    driverContent.put("depLatitude",orderInfo.getDepLatitude());

                    driverContent.put("destination",orderInfo.getDestination());
                    driverContent.put("destLongitude",orderInfo.getDestLongitude());
                    driverContent.put("destLatitude",orderInfo.getDestLatitude());

                    serviceSsePushClient.push(driverId, IdentityConstant.DRIVER_IDENTITY,driverContent.toString());

                    //  通知乘客
                    JSONObject passengerContent = new JSONObject();
                    passengerContent.put("driverId",orderInfo.getDriverId());
                    passengerContent.put("driverPhone",orderInfo.getDriverPhone());
                    passengerContent.put("vehicleNo",orderInfo.getVehicleNo());
                    //  车辆信息，调用服务
                    ResponseResult<Car> carById = serviceDriverUserClient.getCarById(carId);
                    Car remoteCar = carById.getData();

                    passengerContent.put("brand",remoteCar.getBrand());
                    passengerContent.put("model",remoteCar.getModel());
                    passengerContent.put("vehicleColor",remoteCar.getVehicleColor());

                    passengerContent.put("receiveOrderCarLatitude",orderInfo.getReceiveOrderCarLatitude());
                    passengerContent.put("receiveOrderCarLongitude",orderInfo.getReceiveOrderCarLongitude());

                    serviceSsePushClient.push(orderInfo.getPassengerId(), IdentityConstant.PASSENGER_IDENTITY,passengerContent.toString());
                    result = 1;

                    lock.unlock();
                    break;
                }
            }

        }
        return result;
    }

    /**
     * 司机去接乘客
     * @param orderRequest
     * @return
     */
    public ResponseResult toPickUpPassenger(OrderRequest orderRequest){
        Long orderId = orderRequest.getOrderId();
        String toPickUpPassengerAddress = orderRequest.getToPickUpPassengerAddress();
        String toPickUpPassengerLatitude = orderRequest.getToPickUpPassengerLatitude();
        String toPickUpPassengerLongitude = orderRequest.getToPickUpPassengerLongitude();
        LocalDateTime toPickUpPassengerTime = orderRequest.getToPickUpPassengerTime();

        QueryWrapper orderQueryMapper = new QueryWrapper();
        orderQueryMapper.eq("id",orderId);
        OrderInfo orderInfo = orderInfoMapper.selectOne(orderQueryMapper);

        orderInfo.setToPickUpPassengerAddress(toPickUpPassengerAddress);
        orderInfo.setToPickUpPassengerLatitude(toPickUpPassengerLatitude);
        orderInfo.setToPickUpPassengerLongitude(toPickUpPassengerLongitude);
        orderInfo.setToPickUpPassengerTime(toPickUpPassengerTime);
        orderInfo.setOrderStatus(OrderConstant.DRIVER_TO_PICK_UP_PASSENGER);

        orderInfoMapper.updateById(orderInfo);
        return ResponseResult.success();
    }

    /**
     * 司机到达乘客地点
     * @param orderRequest
     * @return
     */
    public ResponseResult arrivedDeparture(OrderRequest orderRequest){
        QueryWrapper orderQueryWrapper = new QueryWrapper();
        orderQueryWrapper.eq("id",orderRequest.getOrderId());
        OrderInfo orderInfo = orderInfoMapper.selectOne(orderQueryWrapper);

        orderInfo.setDriverArrivedDepatureTime(LocalDateTime.now());
        orderInfo.setOrderStatus(OrderConstant.DRIVER_ARRIVED_DEPARTURE);

        orderInfoMapper.updateById(orderInfo);

        return ResponseResult.success();
    }

    /**
     * 司机接到乘客
     * @param orderRequest
     * @return
     */
    public ResponseResult pickUpPassenger(OrderRequest orderRequest){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id",orderRequest.getOrderId());

        String pickUpPassengerLatitude = orderRequest.getPickUpPassengerLatitude();
        String pickUpPassengerLongitude = orderRequest.getPickUpPassengerLongitude();

        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);

        orderInfo.setPickUpPassengerLatitude(pickUpPassengerLatitude);
        orderInfo.setPickUpPassengerLongitude(pickUpPassengerLongitude);
        orderInfo.setPickUpPassengerTime(LocalDateTime.now());
        orderInfo.setOrderStatus(OrderConstant.PICK_UP_PASSENGER);

        orderInfoMapper.updateById(orderInfo);

        return ResponseResult.success();
    }

    /**
     * 乘客到达目的地，行程终止
     * @param orderRequest
     * @return
     */
    public ResponseResult passengerGetOff(OrderRequest orderRequest){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id",orderRequest.getOrderId());

        String passengerGetoffLatitude = orderRequest.getPassengerGetoffLatitude();
        String passengerGetoffLongitude = orderRequest.getPassengerGetoffLongitude();

        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);
        orderInfo.setPassengerGetoffLatitude(passengerGetoffLatitude);
        orderInfo.setPassengerGetoffLongitude(passengerGetoffLongitude);
        orderInfo.setPassengerGetoffTime(LocalDateTime.now());

        orderInfo.setOrderStatus(OrderConstant.PASSENGER_GET_OFF);
        //  订单行驶的时间和路程
        ResponseResult<Car> carById = serviceDriverUserClient.getCarById(orderInfo.getCarId());
        String tid = carById.getData().getTid();
        Long starttime = orderInfo.getPickUpPassengerTime().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        Long endtime = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        ResponseResult<TrsearchResponse> trsearch = serviceMapClient.trsearch(tid, starttime, endtime);

        orderInfo.setDriveMile(trsearch.getData().getDriveMile());
        orderInfo.setDriveTime(trsearch.getData().getDriveTime());

        orderInfoMapper.updateById(orderInfo);
        return ResponseResult.success();
    }
}

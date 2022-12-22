package com.mashibing.serviceorder.service;

import com.mashibing.internalcommon.constant.OrderConstant;
import com.mashibing.internalcommon.dto.OrderInfo;
import com.mashibing.internalcommon.dto.ResponseResult;
import com.mashibing.internalcommon.request.OrderRequest;
import com.mashibing.serviceorder.mapper.OrderInfoMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mutkeb
 * @since 2022-12-22
 */
@Service
public class OrderInfoService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    public ResponseResult add(OrderRequest orderRequest){
        OrderInfo order = new OrderInfo();

        BeanUtils.copyProperties(orderRequest,order);
        order.setOrderStatus(OrderConstant.ORDER_START);

        LocalDateTime now = LocalDateTime.now();
        order.setGmtCreate(now);
        order.setGmtModified(now);

        orderInfoMapper.insert(order);
        return ResponseResult.success();
    }

}

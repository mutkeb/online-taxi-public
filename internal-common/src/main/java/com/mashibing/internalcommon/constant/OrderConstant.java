package com.mashibing.internalcommon.constant;


public class OrderConstant {

    /**
     * 订单无效
     */
    public static final int ORDER_INVALID = 0;

    /**
     * 订单开始
     */
    public static final int ORDER_START = 1;

    /**
     * 司机接单
     */
    public static final int DRIVER_RECEIVE_ORDER = 2;

    /**
     * 司机去接乘客
     */
    public static final int DRIVER_TO_PICK_UP_PASSENGER = 3;

    /**
     * 司机到达乘客起点
     */
    public static final int DRIVER_ARRIVED_DEPARTURE = 4;

    /**
     * 乘客上车：司机开始行程
     */
    public static final int PICK_UP_PASSENGER = 5;

    /**
     * 到达目的地：行程结束，未支付
     */
    public static final int PASSENGER_GET_OFF = 6;

    /**
     * 发起收款
     */
    public static final int TO_START_PAY = 7;

    /**
     * 支付完成
     */
    public static final int SUCCESS_PAY = 8;

    /**
     * 订单取消
     */
    public static final int CANCEL = 9;

    /**
     * 乘客提前取消
     */
    public static final int CANCEL_PASSENGER_BEFORE = 1;

    /**
     * 司机提前取消
     */
    public static final int CANCEL_DRIVER_BEFORE = 2;

    /**
     * 平台公司取消
     */
    public static final int CANCEL_PLATFORM_BEFORE = 3;

    /**
     * 乘客违约取消
     */
    public static final int CANCEL_PASSENGER_ILLEGAL = 4;

    /**
     * 驾驶员违约取消
     */
    public static final int CANCEL_DRIVER_ILLEGAL = 5;
}

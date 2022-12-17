package com.mashibing.internalcommon.constant;

public class DriverCarConstant {

    /**
     * 司机车辆信息绑定
     */
    public static int DRIVER_CAR_BIND = 1;

    /**
     * 司机车辆信息解绑
     */
    public static int DRIVER_CAR_UNBIND = 2;

    /**
     * 司机状态：1：有效
     */

    public static int DRIVER_STATE_VALID = 1;

    /**
     * 司机状态：0：有效
     */

    public static int DRIVER_STATE_INVALID = 0;

    /**
     * 司机状态: 1: 存在
     */

    public static int DRIVER_EXISTS = 1;

    /**
     * 司机状态 0: 不存在
     */
    public static int DRIVER_NOT_EXISTS = 0;

    /**
     * 司机工作状态 0:收车
     */
    public static int DRIVER_WORK_STATUS_STOP = 0;

    /**
     * 司机工作状态 1:出车
     */
    public static int DRIVER_WORK_STATUS_START = 1;

    /**
     * 司机工作状态 2:暂停
     */
    public static int DRIVER_WORK_STATUS_SUSPEND = 2;
}

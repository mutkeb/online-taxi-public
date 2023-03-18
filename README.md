# -网约车项目代码-
关于网约车项目分布式技术应用的整体代码

功能描述：

1.乘客端：登录注册、下单、支付、评价、维护个人信息
2.司机端：登录、出车收车、发起收款、订单完成
3.车载大屏：同步司机和乘客信息、广告投放
4.运营平台：进行计价规则编辑等操作

主要包括以下模块：用户模块、地图模块、消息模块、支付模块、计价模块、账号系统等

使用到的技术：SpringCloud常用中间件（Nacos、OpenFeign）、RocketMQ中间件、分布式事务（Seata组件）、Mysql  及Reids

部分功能展示：

乘客叫车：
![image](https://user-images.githubusercontent.com/103638902/226091368-1b760977-7a76-4ebe-b815-ee681dc524e0.png)

司机接单：
![image](https://user-images.githubusercontent.com/103638902/226091434-64ec4195-b118-4068-bd00-5b11ec702098.png)

司机发起收款:
![image](https://user-images.githubusercontent.com/103638902/226091459-dae97e15-0167-4001-ba4c-40092da04b54.png)

乘客支付：
![image](https://user-images.githubusercontent.com/103638902/226091486-f4431937-c583-4466-a474-f13706c7c5aa.png)

package edu.bupt.sv.core;

public interface MsgConstants {
	// 预留的空消息
	public static final int MSG_NONE = -1;
	// 初始化
	public static final int MSG_INIT_THREAD = 0;
	// 初始化车辆
	public static final int MSG_INIT_VEHICLE = 1;
	// 订阅车辆信息
	public static final int MSG_SUBSCRIBE_INFO = 2;
	// 获取路径规划
	public static final int MSG_PATH_PLAN = 3;
	// 有序充电服务
	public static final int MSG_REQUEST_CHARGE = 4;
	// 改变路径
	public static final int MSG_CHANGE_PATH = 5;
	// 改变终点
	public static final int MSG_CHANGE_DEST = 6;
	// 获取车辆列表
	public static final int MSG_VEHICLE_LIST = 7;
	// 错误
	public static final int MSG_ON_ERROR = 8;
	// 收到数据
	public static final int MSG_ON_RECEIVE = 9;
	// 销毁并退出
	public static final int MSG_ON_QUIT	= 10;
	
	// 错误数据
	public static final int DATA_ERROR = 0;
	// 车辆列表数据标识
	public static final int DATA_VEHICLE_LIST = 1;
	// 车辆订阅数据标识
	public static final int DATA_VEHICLE_INFO = 2;
	// 路径规划数据标识
	public static final int DATA_PATH_PLAN = 3;
}

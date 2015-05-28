package edu.bupt.sv.service;

public interface NetworkConstants {

	public final String TM_HOST = "10.108.121.112";
	public final int TM_PORT = 8888;
	
	public final String IOV_HOST = "http://10.108.120.176:8080";
	
	// TM相关参数
	// TM响应码
	// 成功取得数据或成功结束指令同步
	public final int RES_SUCCESS_CODE = 200;
	// 成功取得部分数据
	public final int RES_PART_SUCCESS_CODE = 206;
	// 请求消息错误
	public final int RES_ERROR_CODE = 400;
	// 没有需要的数据
	public final int RES_NO_DATA_CODE = 404;
	
	// 车联网服务相关参数
	// http连接retry次数
	public final int MAX_RETRY_TIMES = 3;
	// http连接超时时间(毫秒)
	public final int SERVICE_TIMEOUT = 10000;
}

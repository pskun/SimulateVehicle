package edu.bupt.sv.service;

public interface NetworkConstants {

	public final String TM_HOST = "10.108.121.112";
	public final int TM_PORT = 8888;
	
	public final String IOV_HOST = "";
	
	// 响应码
	// 成功取得数据或成功结束指令同步
	public final int RES_SUCCESS_CODE = 200;
	// 请求消息错误
	public final int RES_ERROR_CODE = 400;
	// 没有需要的数据
	public final int RES_NO_DATA_CODE = 404;
}

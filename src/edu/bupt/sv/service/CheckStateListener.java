/**
 * 
 */
package edu.bupt.sv.service;

/**
 * @author pankunhao
 * 检查连接状态的回调
 */

public interface CheckStateListener {
	
	/**
	 * 新增的接口, 2015/09/14
	 * 根据需求，需要首先连接服务，待服务连接成功后再进行其他操作
	 * @param status 当前完成的状态
	 */
	void onInitStatus(int status);
}

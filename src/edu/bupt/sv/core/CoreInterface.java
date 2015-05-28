package edu.bupt.sv.core;

import android.util.SparseArray;

import edu.bupt.sv.entity.Node;
import edu.bupt.sv.entity.Vehicle;

public interface CoreInterface {
	/**
	 * 初始化API接口
	 * @return
	 */
	public boolean initApi();
	
	/**
	 * @deprecated
	 * 获得车辆信息列表
	 * 已废止，改由本地读文件实现
	 */
	public void requestVehicleList();
	
	/**
	 * 初始化一辆车，开始订阅该车的信息
	 * @param vehicleId 车辆Id
	 * @return
	 */
	public boolean initVehicle(Integer vehicleId);
	
	/**
	 * 转向
	 * @param direction
	 * @return
	 */
	public boolean turnNewPath(int direction);
	
	/**
	 * 改变终点
	 * @param newDestNodeId
	 * @return
	 */
	public boolean changeDestination(Integer newDestNodeId);
	
	/**
	 * 请求有序充电服务
	 */
	public void requestCharge();
	
	/**
	 * 销毁api接口
	 */
	public void destroyApi();
	
	/**
	 * 非异步方法，获得vehicle的列表
	 * @return
	 */
	public SparseArray<Vehicle> getVehicleList();
	
	/**
	 * 非异步方法，获得node的列表
	 * @return
	 */
	public SparseArray<Node> getNodeList();
	
	/**
	 * 非异步方法，获得充电站的列表
	 * @return
	 */
	public SparseArray<Node> getChargeStation();
}

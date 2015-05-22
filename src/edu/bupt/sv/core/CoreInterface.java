package edu.bupt.sv.core;

import android.util.SparseArray;

import edu.bupt.sv.entity.Node;
import edu.bupt.sv.entity.Vehicle;

public interface CoreInterface {
	// 初始化底层的API
	public boolean initApi();
	
	public void requestVehicleList();
	
	public boolean initVehicle(Integer vehicleId);
	
	public boolean turnNewPath(int direction);
	
	public boolean changeDestination(Integer newDestNodeId);
	
	public void requestCharge();
	
	public void destroyApi();
	
	/**
	 * 不是异步的方法，获得vehicle的列表
	 * @return
	 */
	public SparseArray<Vehicle> getVehicleList();
	
	/**
	 * 非异步方法，获得node的列表
	 * @return
	 */
	public SparseArray<Node> getNodeList();
}

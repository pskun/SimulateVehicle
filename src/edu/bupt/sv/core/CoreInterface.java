package edu.bupt.sv.core;

import android.util.SparseArray;

import edu.bupt.sv.entity.Node;
import edu.bupt.sv.entity.Vehicle;

public interface CoreInterface {
	// ��ʼ���ײ��API
	public boolean initApi();
	
	public void requestVehicleList();
	
	public boolean initVehicle(Integer vehicleId);
	
	public boolean turnNewPath(int direction);
	
	public boolean changeDestination(Integer newDestNodeId);
	
	public void requestCharge();
	
	public void destroyApi();
	
	/**
	 * �����첽�ķ��������vehicle���б�
	 * @return
	 */
	public SparseArray<Vehicle> getVehicleList();
	
	/**
	 * ���첽���������node���б�
	 * @return
	 */
	public SparseArray<Node> getNodeList();
}

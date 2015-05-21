package edu.bupt.sv.core;

import java.util.List;

import android.util.SparseArray;

import edu.bupt.sv.entity.Node;
import edu.bupt.sv.entity.Point;
import edu.bupt.sv.entity.Vehicle;

public interface CoreListener {
	
	void onRecvVehicleList(List<Integer> vehicleIds);
	
	void onLocationChanged(Point newPoint);
	
	void onChargedChanged(double charge);
	
	/**
	 * ���¹滮·����Ļص�
	 * @param paths ·���ϵĽڵ�ľ�γ��
	 * @param success �滮�ɹ�����true������滮���߹滮ʧ�ܷ���false
	 * ע��: 
	 */
	void onPathChanged(boolean success, List<Point> paths);
	
	void onDestChanged(Point newDest, List<Point> paths);
	
	void onError(int errorCode);
	
	void onInitFinish(SparseArray<Node> nodes, SparseArray<Vehicle> vehicles);
	
}

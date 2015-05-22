package edu.bupt.sv.core;

import java.util.List;

import android.util.SparseArray;

import edu.bupt.sv.entity.Node;
import edu.bupt.sv.entity.Point;
import edu.bupt.sv.entity.Vehicle;

public interface CoreListener {
	
	void onRecvVehicleList(List<Integer> vehicleIds);
	
	/**
	 * ������γ�ȱ仯ʱ�Ļص�
	 * @param newPoint
	 */
	void onLocationChanged(Point newPoint);
	
	/**
	 * ���������仯ʱ�Ļص�
	 * @param charge
	 */
	void onChargedChanged(double charge);
	
	/**
	 * ���¹滮·����Ļص�
	 * @param paths ·���ϵĽڵ�ľ�γ��
	 * @param success �滮�ɹ�����true������滮���߹滮ʧ�ܷ���false
	 */
	void onPathChanged(boolean success, List<Point> paths, Point start, Point end);
	
	void onError(int errorCode);
	
	void onInitFinish(SparseArray<Node> nodes, SparseArray<Vehicle> vehicles);
	
}

package edu.bupt.sv.core;

import java.util.List;

import android.util.SparseArray;

import edu.bupt.sv.entity.Node;
import edu.bupt.sv.entity.Point;
import edu.bupt.sv.entity.Vehicle;

public interface CoreListener {
	
	void onRecvVehicleList(List<Integer> vehicleIds);
	
	/**
	 * 车辆经纬度变化时的回调
	 * @param newPoint
	 */
	void onLocationChanged(Point newPoint);
	
	/**
	 * 车辆电量变化时的回调
	 * @param charge
	 */
	void onChargedChanged(double charge);
	
	/**
	 * 重新规划路径后的回调
	 * @param paths 路径上的节点的经纬度
	 * @param success 规划成功返回true，无需规划或者规划失败返回false
	 */
	void onPathChanged(boolean success, List<Point> paths, Point start, Point end);
	
	void onError(int errorCode);
	
	void onInitFinish(SparseArray<Node> nodes, SparseArray<Vehicle> vehicles);
	
}

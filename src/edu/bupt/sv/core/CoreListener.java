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
	 * 重新规划路径后的回调
	 * @param paths 路径上的节点的经纬度
	 * @param success 规划成功返回true，无需规划或者规划失败返回false
	 * 注意: 
	 */
	void onPathChanged(boolean success, List<Point> paths);
	
	void onDestChanged(Point newDest, List<Point> paths);
	
	void onError(int errorCode);
	
	void onInitFinish(SparseArray<Node> nodes, SparseArray<Vehicle> vehicles);
	
}

package edu.bupt.sv.core;

import java.util.List;

import android.util.SparseArray;

import edu.bupt.sv.entity.Node;
import edu.bupt.sv.entity.Point;
import edu.bupt.sv.entity.Vehicle;

public interface CoreListener {
	
	/**
	 * @deprecated
	 * @param vehicleIds
	 */
	void onRecvVehicleList(List<Integer> vehicleIds);
	
	/**
	 * 车辆经纬度变化时的回调
	 * @param newPoint
	 */
	void onLocationChanged(Point newPoint);
	
	/**
	 * 车辆其他变化时的回调
	 * @param charge
	 */
	void onOtherInfoChanged(double charge, double speed, Integer linkID ,Integer status);
	
	/**
	 * 重新规划路径后的回调
	 * @param paths 路径上的节点的经纬度
	 * @param success 规划成功返回true，无需规划或者规划失败返回false
	 */
	void onPathChanged(boolean success, List<Point> paths, Point start, Point end);
	
	/**
	 * 错误发生时的回调
	 * @param errorCode 见ErrorConstants.java
	 */
	void onError(int errorCode);

	// 以下是调试时的临时接口
	/**
	 * 获得转向的路口的经纬度
	 * @param crossPoint
	 */
	void onGetTurnNodeId(Point crossPoint, Point newStartPoint);
}

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
	 * ������γ�ȱ仯ʱ�Ļص�
	 * @param newPoint
	 */
	void onLocationChanged(Point newPoint);
	
	/**
	 * ���������仯ʱ�Ļص�
	 * @param charge
	 */
	void onOtherInfoChanged(double charge, double speed, Integer linkID ,Integer status);
	
	/**
	 * ���¹滮·����Ļص�
	 * @param paths ·���ϵĽڵ�ľ�γ��
	 * @param success �滮�ɹ�����true������滮���߹滮ʧ�ܷ���false
	 */
	void onPathChanged(boolean success, List<Point> paths, Point start, Point end);
	
	/**
	 * ������ʱ�Ļص�
	 * @param errorCode ��ErrorConstants.java
	 */
	void onError(int errorCode);

	// �����ǵ���ʱ����ʱ�ӿ�
	/**
	 * ���ת���·�ڵľ�γ��
	 * @param crossPoint
	 */
	void onGetTurnNodeId(Point crossPoint, Point newStartPoint);
}

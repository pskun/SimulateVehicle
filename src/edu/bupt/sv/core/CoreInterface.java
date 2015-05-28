package edu.bupt.sv.core;

import android.util.SparseArray;

import edu.bupt.sv.entity.Node;
import edu.bupt.sv.entity.Vehicle;

public interface CoreInterface {
	/**
	 * ��ʼ��API�ӿ�
	 * @return
	 */
	public boolean initApi();
	
	/**
	 * @deprecated
	 * ��ó�����Ϣ�б�
	 * �ѷ�ֹ�����ɱ��ض��ļ�ʵ��
	 */
	public void requestVehicleList();
	
	/**
	 * ��ʼ��һ��������ʼ���ĸó�����Ϣ
	 * @param vehicleId ����Id
	 * @return
	 */
	public boolean initVehicle(Integer vehicleId);
	
	/**
	 * ת��
	 * @param direction
	 * @return
	 */
	public boolean turnNewPath(int direction);
	
	/**
	 * �ı��յ�
	 * @param newDestNodeId
	 * @return
	 */
	public boolean changeDestination(Integer newDestNodeId);
	
	/**
	 * �������������
	 */
	public void requestCharge();
	
	/**
	 * ����api�ӿ�
	 */
	public void destroyApi();
	
	/**
	 * ���첽���������vehicle���б�
	 * @return
	 */
	public SparseArray<Vehicle> getVehicleList();
	
	/**
	 * ���첽���������node���б�
	 * @return
	 */
	public SparseArray<Node> getNodeList();
	
	/**
	 * ���첽��������ó��վ���б�
	 * @return
	 */
	public SparseArray<Node> getChargeStation();
}

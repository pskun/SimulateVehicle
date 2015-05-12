package edu.bupt.sv.core;

public interface CoreInterface {
	// ��ʼ���ײ��API
	public boolean initApi();
	
	public void requestVehicleList();
	
	public boolean initVehicle();
	
	public boolean turnNewPath();
	
	public void changeDestination();
	
	public void requestCharge();
	
	public boolean getVehicleList();
}

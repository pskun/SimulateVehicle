package edu.bupt.sv.core;

public interface CoreInterface {
	// ³õÊ¼»¯µ×²ãµÄAPI
	public boolean initApi();
	
	public void requestVehicleList();
	
	public boolean initVehicle();
	
	public boolean turnNewPath();
	
	public void changeDestination();
	
	public void requestCharge();
	
	public boolean getVehicleList();
}

package edu.bupt.sv.core;

public interface CoreInterface {
	// ³õÊ¼»¯µ×²ãµÄAPI
	public boolean initApi();
	
	public void requestVehicleList();
	
	public boolean initVehicle(Integer vehicleId);
	
	public boolean turnNewPath(int direction);
	
	public boolean changeDestination(Integer newDestNodeId);
	
	public void requestCharge();
	
	public void destroyApi();
}

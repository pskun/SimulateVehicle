package edu.bupt.sv.control;

import edu.bupt.sv.entity.Vehicle;
import edu.bupt.sv.utils.LogUtil;

public class VehicleController {
	
	private Vehicle vehicle;
	
	public VehicleController() {
		this.vehicle = new Vehicle();
	}
	
	public VehicleController(Vehicle vehicle) {
		super();
		this.vehicle = vehicle;
	}

	// Link–≈œ¢
	
	public void setLocation(double latitude, double longitude) {
		if (vehicle != null) {
			vehicle.setLatitude(latitude);
			vehicle.setLongitude(longitude);
		}
		else {
			LogUtil.warn("entity vehicle is null.");
		}
	}
	
	public void start() {
		
	}
	
	public void pause() {
		
	}
	
	public void stop() {
		
	}
}

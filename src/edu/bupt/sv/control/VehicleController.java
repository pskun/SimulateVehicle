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

	// Link��Ϣ
	
	/**
	 * ����λ�ã����λ���и����򷵻�true�����򷵻�false
	 * @param latitude
	 * @param longitude
	 */
	public boolean setLocation(double latitude, double longitude) {
		if (vehicle != null) {
			if(vehicle.getLatitude() != latitude || vehicle.getLongitude() != longitude) {
				vehicle.setLatitude(latitude);
				vehicle.setLongitude(longitude);
				return true;
			}
		}
		else {
			LogUtil.warn("entity vehicle is null.");
		}
		return false;
	}
	
	public void start() {
		
	}
	
	public void pause() {
		
	}
	
	public void stop() {
		
	}
}

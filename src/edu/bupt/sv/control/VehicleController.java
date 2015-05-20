package edu.bupt.sv.control;

import edu.bupt.sv.entity.Vehicle;
import edu.bupt.sv.utils.LogUtil;

public class VehicleController {
	
	private Object lock = new Object();
	
	private Vehicle vehicle;
	
	public VehicleController() {
		this.vehicle = new Vehicle();
	}
	
	public VehicleController(Vehicle vehicle) {
		super();
		this.vehicle = vehicle;
	}

	public void setVehicle(Vehicle v) {
		this.vehicle = v;
	}
	
	// Link��Ϣ
	
	/**
	 * ���ó���ID���������ID�����򷵻�true�����򷵻�false
	 */
	public boolean setId(Integer vehicleId) {
		if (vehicle != null) {
			if(vehicle.getId() != vehicleId) {
				synchronized (lock) {
					vehicle.setId(vehicleId);
				}
				return true;
			}
		}
		else {
			LogUtil.warn("entity vehicle is null.");
		}
		return false;
	}
	
	public Integer getId() {
		if (vehicle == null)
			return null;
		Integer id = null;
		synchronized (lock) {
			id = vehicle.getId();
		}
		return id;
	}
	
	/**
	 * ����λ�ã����λ���и����򷵻�true�����򷵻�false
	 * @param latitude
	 * @param longitude
	 */
	public boolean setLocation(double latitude, double longitude) {
		if (vehicle != null) {
			if(vehicle.getLatitude() != latitude || vehicle.getLongitude() != longitude) {
				synchronized (lock) {
					vehicle.setLatitude(latitude);
					vehicle.setLongitude(longitude);
				}
				return true;
			}
		}
		else {
			LogUtil.warn("entity vehicle is null.");
		}
		return false;
	}
	
	public boolean setCharge(double charge){
		if (vehicle != null) {
			if(vehicle.getCharge() != charge) {
				vehicle.setCharge(charge);
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

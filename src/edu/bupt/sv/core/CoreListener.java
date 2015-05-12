package edu.bupt.sv.core;

import java.util.List;

public interface CoreListener {
	
	void onRecvVehicleList(List<Integer> vehicleIds);
	
	void onLocationChanged(double latitude, double longitude);
	
	void onChargedChanged(double charge);
	
	void onPathChanged();
	
	void onDestChanged(double destLat, double destLng);
	
	void onError(int errorCode);
	
}

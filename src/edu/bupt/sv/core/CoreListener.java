package edu.bupt.sv.core;

import java.util.List;

import edu.bupt.sv.entity.Point;

public interface CoreListener {
	
	void onRecvVehicleList(List<Integer> vehicleIds);
	
	void onLocationChanged(Point newPoint);
	
	void onChargedChanged(double charge);
	
	void onPathChanged(List<Point> paths);
	
	void onDestChanged(Point newDest, List<Point> paths);
	
	void onError(int errorCode);
	
}

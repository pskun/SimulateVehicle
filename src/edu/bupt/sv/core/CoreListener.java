package edu.bupt.sv.core;

import java.util.List;

import android.util.SparseArray;

import edu.bupt.sv.entity.Node;
import edu.bupt.sv.entity.Point;
import edu.bupt.sv.entity.Vehicle;

public interface CoreListener {
	
	void onRecvVehicleList(List<Integer> vehicleIds);
	
	void onLocationChanged(Point newPoint);
	
	void onChargedChanged(double charge);
	
	void onPathChanged(List<Point> paths);
	
	void onDestChanged(Point newDest, List<Point> paths);
	
	void onError(int errorCode);
	
	void onInitFinish(SparseArray<Node> nodes, SparseArray<Vehicle> vehicles);
	
}

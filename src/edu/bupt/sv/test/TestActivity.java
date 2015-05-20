package edu.bupt.sv.test;

import java.util.List;

import edu.bupt.sv.core.CoreApi;
import edu.bupt.sv.core.CoreListener;
import edu.bupt.sv.entity.Node;
import edu.bupt.sv.entity.Point;
import edu.bupt.sv.entity.Vehicle;
import edu.bupt.sv.ui.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestActivity extends Activity {
	
	private Button vehicleListBtn;
	private Button quitBtn;
	private Button subVehicleBtn;
	private Button initBtn;

	private Context mContext;
	private CoreApi api;
	

	private CoreListener coreListener = new CoreListener() {
		
		@Override
		public void onRecvVehicleList(List<Integer> vehicleIds) {
			int size = vehicleIds.size();
			for(int i=0; i<size; i++) {
				System.out.println("Vehicle id: " + vehicleIds.get(i));
			}
		}

		@Override
		public void onLocationChanged(Point newPoint) {
			System.out.println("Current Location: " + newPoint.latitude + " " + newPoint.longitude);
		}

		@Override
		public void onChargedChanged(double charge) {
			System.out.println("Current charge: " + charge);
		}

		@Override
		public void onPathChanged(List<Point> paths) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDestChanged(Point newDest, List<Point> paths) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onError(int errorCode) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onInitFinish(SparseArray<Node> nodes,
				SparseArray<Vehicle> vehicles) {
			// TODO Auto-generated method stub
			
			
			
		}
		
	};
	
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int clickId = v.getId();
			switch(clickId)
			{
			case R.id.init_btn:
				init();
				break;
			case R.id.vehicle_list_btn:
				requestVehicleList();
				break;
			case R.id.quit_btn:
				quit();
				break;
			case R.id.sub_vehicle_btn:
				subVehicleInfo(1);
				break;
			}
		}
	};
	
	private void init() {
		api = new CoreApi(mContext);
		api.setListener(coreListener);
		api.initApi();
	}
	
	private void requestVehicleList() {
		api.requestVehicleList();
	}
	
	private void subVehicleInfo(Integer vehicleId) {
		api.initVehicle(vehicleId);
	}
	
	private void quit() {
		api.destroyApi();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		
		this.mContext = this.getApplicationContext();
		
		initBtn = (Button) findViewById(R.id.init_btn);
		initBtn.setOnClickListener(listener);
		
		vehicleListBtn = (Button) findViewById(R.id.vehicle_list_btn);
		vehicleListBtn.setOnClickListener(listener);
		
		quitBtn = (Button) findViewById(R.id.quit_btn);
		quitBtn.setOnClickListener(listener);
		
		subVehicleBtn = (Button) findViewById(R.id.sub_vehicle_btn);
		subVehicleBtn.setOnClickListener(listener);
		
	}
}

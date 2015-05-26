package edu.bupt.sv.test;

import java.util.List;

import edu.bupt.sv.core.ApiFactory;
import edu.bupt.sv.core.CoreApi;
import edu.bupt.sv.core.CoreListener;
import edu.bupt.sv.entity.Node;
import edu.bupt.sv.entity.Point;
import edu.bupt.sv.entity.Vehicle;
import edu.bupt.sv.ui.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TestActivity extends Activity {
	
	private static final int MSG_ON_LOCATION_CHANGE = 1;
	
	private Button vehicleListBtn;
	private Button quitBtn;
	private Button subVehicleBtn;
	private Button initBtn;
	private Button turnNewPathBtn;
	private Button changeDestBtn;
	
	private TextView latText;
	private TextView lngText;
	
	private Context mContext;
	private CoreApi api;
	

	private Handler uiHandler = new Handler(getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			handleLocalMessage(msg);
		}
	};
	
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
			uiHandler.obtainMessage(MSG_ON_LOCATION_CHANGE, newPoint);
		}

		@Override
		public void onError(int errorCode) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPathChanged(boolean success, List<Point> paths,
				Point start, Point end) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onOtherInfoChanged(double charge, double speed,
				Integer linkId) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetTurnNodeId(Point crossPoint, Point newStartPoint) {
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
				subVehicleInfo(10);
				break;
			}
		}
	};
	
	private void init() {
		api = ApiFactory.getInstance(mContext);
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
		
		turnNewPathBtn = (Button) findViewById(R.id.turn_new_path_btn);
		turnNewPathBtn.setOnClickListener(listener);
		
		changeDestBtn = (Button) findViewById(R.id.change_dest_btn);
		changeDestBtn.setOnClickListener(listener);
		
		latText = (TextView) findViewById(R.id.lat_text);
		lngText = (TextView) findViewById(R.id.lng_text);
		
	}
	
	private void handleLocalMessage(Message msg) {
		switch(msg.what) {
		case MSG_ON_LOCATION_CHANGE:
			handleOnlocationChanged((Point) msg.obj);
			break;
		}
	}
	
	private void handleOnlocationChanged(Point newPoint) {
		latText.setText("Î³¶È: " + newPoint.latitude);
		lngText.setText("¾­¶È: " + newPoint.longitude);
	}
}

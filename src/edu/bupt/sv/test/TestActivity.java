package edu.bupt.sv.test;

import java.util.List;

import edu.bupt.sv.core.CoreApi;
import edu.bupt.sv.core.CoreListener;
import edu.bupt.sv.utils.LogUtil;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestActivity extends Activity {
	
	private Button vehicleListBtn;
	private Button quitBtn;

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
		public void onPathChanged() {}
		
		@Override
		public void onLocationChanged(double latitude, double longitude) {}
		
		@Override
		public void onError(int errorCode) {}
		
		@Override
		public void onDestChanged(double destLat, double destLng) {}
		
		@Override
		public void onChargedChanged(double charge) {}
	};
	
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int clickId = v.getId();
			switch(clickId)
			{
			case R.id.vehicle_list_btn:
				requestVehicleList();
				break;
			case R.id.quit_btn:
				quit();
				break;
			}
		}
	};
	
	private void init() {
		api = new CoreApi();
		api.setListener(coreListener);
		api.initApi();
	}
	
	private void requestVehicleList() {
		api.requestVehicleList();
	}
	
	private void quit() {
		api.destroyApi();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		
		vehicleListBtn = (Button) findViewById(R.id.vehicle_list_btn);
		vehicleListBtn.setOnClickListener(listener);
		
		quitBtn = (Button) findViewById(R.id.quit_btn);
		quitBtn.setOnClickListener(listener);
		//test
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}
}

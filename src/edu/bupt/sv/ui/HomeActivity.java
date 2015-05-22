package edu.bupt.sv.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.bupt.sv.core.ApiFactory;
import edu.bupt.sv.core.CoreApi;
import edu.bupt.sv.core.CoreListener;
import edu.bupt.sv.entity.Node;
import edu.bupt.sv.entity.Point;
import edu.bupt.sv.entity.Vehicle;

import edu.bupt.sv.utils.LogUtil;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class HomeActivity extends Activity {
	private ListView vehicleList;
	
	private Context mContext;
	private CoreApi api;
	private Handler myHandler;
	
	
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
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onChargedChanged(double charge) {
			// TODO Auto-generated method stub
			
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
		public void onInitFinish(SparseArray<Node> nodes,
				SparseArray<Vehicle> vehicles) {
			// TODO Auto-generated method stub		
			sendMessage(1, vehicles);
		}

		
	};
	
	private OnItemClickListener  listener = new OnItemClickListener()  {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			clickList(view);
			//System.out.println("111111111111111111"+position);
             		
		}
	};
	
	private void init() {
		api = ApiFactory.getInstance(mContext);
		api.setListener(coreListener);
		api.initApi();
	}
		
	public void sendMessage(int msgCode) {
		if(myHandler == null) {
			LogUtil.warn("uiThread handler is null. Unable to send message.");
			return;
		}
		myHandler.obtainMessage(msgCode).sendToTarget();
	}
	
	public void sendMessage(int msgCode, Object object) {
		if(myHandler == null) {
			LogUtil.warn("uiThread handler is null. Unable to send message.");
			return;
		}
		myHandler.obtainMessage(msgCode, object).sendToTarget();
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.home_page);
		
		this.mContext = this.getApplicationContext();
		//加载放在onInitFinish中进行
		myHandler =new Handler(){
			   @Override
	            public void handleMessage(Message msg) {
	                super.handleMessage(msg);
	                switch(msg.what)
	        		{
	        		case  1 :
	        			loadInfo((SparseArray<Vehicle>) msg.obj);
	        			break;
	        		}
	            }
		};
		init();
	}
	
	private void loadInfo (SparseArray<Vehicle> vehicles){

		int[] imageIds = new int[]{R.drawable.type0};
		List<Map <String,Object>> listItems = new ArrayList<Map<String ,Object>>();
		for (int i=0;i<vehicles.size();i++){
			Map<String , Object> listItem = new HashMap<String,Object>();
			listItem.put("header",imageIds[0]);
			listItem.put("vehicleId","车辆ID:　"+i);
			Vehicle curVehicle = vehicles.get(i);
			listItem.put("desc","车辆型号："+curVehicle.getModel()+ "\n总电量 ：" + curVehicle.getTotalEnergy()
					+ "\n剩余电量：" +curVehicle.getCharge() + "\n运行速度 :　"+ curVehicle.getSpeed());
			listItems.add(listItem);
		}
		
		SimpleAdapter simpleAdapter = new SimpleAdapter(this,listItems,
				R.layout.list_style,
				new String[]{"vehicleId","header","desc"},
				new int[]{ R.id.name,R.id.header,R.id.desc});
		
		vehicleList = (ListView) findViewById(R.id.mylist);
		vehicleList.setAdapter(simpleAdapter);
		vehicleList.setOnItemClickListener(listener);
		
	}
	
	
	public void clickList (View view){
		Intent intent = new Intent();
    	intent.setClassName(this, "edu.bupt.sv.ui.FunctionActivity");
    	startActivity(intent);
	}
	


}

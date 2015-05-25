package edu.bupt.sv.ui;

import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import edu.bupt.sv.core.ApiFactory;
import edu.bupt.sv.core.CoreApi;
import edu.bupt.sv.core.CoreListener;
import edu.bupt.sv.entity.Node;
import edu.bupt.sv.entity.Point;
import edu.bupt.sv.entity.Vehicle;
import edu.bupt.sv.utils.LogUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupMenu;

public class FunctionActivity extends Activity  implements OnMapReadyCallback{
	private static final int MSG_ON_LOCATION_CHANGE = 1;
	private static final int MSG_ON_PATH_CHANGE =2;
	private static final int MSG_ON_CHARGE_CHANGE =3;
	
	static final LatLng NKUT = new LatLng(23.979548, 120.696745);
    private GoogleMap map;
    private Context mContext;
    private CoreApi api;
    private int vehicleid;
    private Marker carPosition;
    
    PopupMenu popup = null;
    
	private Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			handleLocalMessage(msg);
		}
	};
    
	private void init() {
		api = ApiFactory.getInstance(mContext);
		api.setListener(coreListener);
		//System.out.println("1111118");
		api.initApi();
	}
    
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
			uiHandler.obtainMessage(MSG_ON_LOCATION_CHANGE, newPoint).sendToTarget();
		}

		@Override
		public void onChargedChanged(double charge) {
			System.out.println("Current charge: " + charge);
			uiHandler.obtainMessage(MSG_ON_CHARGE_CHANGE,charge).sendToTarget();
			
		}

		@Override
		public void onError(int errorCode) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onPathChanged(boolean success, List<Point> paths,
				Point start, Point end) {
			// TODO Auto-generated method stub
			uiHandler.obtainMessage(MSG_ON_PATH_CHANGE,paths).sendToTarget();
			//System.out.println("111111#33");
			uiHandler.obtainMessage(MSG_ON_LOCATION_CHANGE,start).sendToTarget();
		
		}
		@Override
		public void onInitFinish(SparseArray<Node> nodes,
				SparseArray<Vehicle> vehicles) {
			// TODO Auto-generated method stub
		}
		
	};
	
    private OnClickListener listener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			int clickId = v.getId();
			switch(clickId)
			{
			case R.id.buttonservice:
				//getService();
				break;
			case R.id.vehicle_list_btn:
				//requestVehicleList();
				break;
			case R.id.quit_btn:
				//quit();
				break;
			case R.id.sub_vehicle_btn:
				//subVehicleInfo(10);
				break;
			}
		}
	};
	
	
   @Override
    protected void onCreate(Bundle savedInstanceState) {	 
         super.onCreate(savedInstanceState);
         setContentView(R.layout.function_page); 
         
         this.mContext = this.getApplicationContext();
         vehicleid = getIntent().getIntExtra("id", 0);
         init();
        ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        
   	     final  Button moreMenu = (Button)this.findViewById(R.id.buttonservice);
         moreMenu.setOnClickListener(new OnClickListener() {
         
         @Override
         public void onClick(View moreMenu){
                 PopupMenu popup = new PopupMenu(FunctionActivity.this, moreMenu);
                 popup.getMenuInflater()
                     .inflate(R.layout.service_menu, popup.getMenu());

                 //registering popup with OnMenuItemClickListener
                 popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                     public boolean onMenuItemClick(MenuItem item) {
                
                         return true;
                     }
                 });
                 popup.show(); //showing popup menu 
                     }
               });                  
    }

	@Override
	public void onMapReady(GoogleMap map) {
		
	  this.map = map;
	  SparseArray<Node> nodeInfo = api.getNodeList();  
      for(int i=0;i<nodeInfo.size();i++){ 	 
    	  Node node = nodeInfo.valueAt(i);
    	  map.addMarker(new MarkerOptions().position(new LatLng(node.getLatitude(),node.getLongitude()))
    	    		  .title("NODE").snippet(String.valueOf(node.getId()))
    	    		  .icon(BitmapDescriptorFactory.fromResource(R.drawable.node)));
      }  
      LatLng NKUT = new LatLng(29.542324,-98.576859);      
      map.moveCamera(CameraUpdateFactory.newLatLngZoom(NKUT, 16));
      //System.out.println("111111#14");
      api.initVehicle(vehicleid);
      
	}
	
	private void handleLocalMessage(Message msg) {
		switch(msg.what) {
		case MSG_ON_PATH_CHANGE:
			//System.out.println("111111#31");
			handleOnPathChanged((List<Point>) msg.obj);			
			break;
		case MSG_ON_LOCATION_CHANGE:
			System.out.println("111111#32");
			handleOnLocationChanged((Point) msg.obj);
			break;
		case MSG_ON_CHARGE_CHANGE:
			handleOnChargeChanged(msg.obj);
		}
	}
	
	private void handleOnChargeChanged(Object obj) {
		// TODO Auto-generated method stub
		
	}

	private void handleOnPathChanged(List<Point> paths){
		//System.out.println("111111#35");
		PolylineOptions rectOptions = new PolylineOptions();
		for(int i=0;i<paths.size();i++){
			rectOptions.add(new LatLng(paths.get(i).latitude,paths.get(i).longitude));			
		}
		Polyline polyline = map.addPolyline(rectOptions);
	}
	
	private void handleOnLocationChanged(Point newPoint){
		System.out.println("111111#36");
		if(carPosition!=null){
		carPosition.remove();}
		LatLng Position = new LatLng(newPoint.latitude, newPoint.longitude);  
		carPosition= map.addMarker(new MarkerOptions()  
        .position(Position)); 
		//map.moveCamera(CameraUpdateFactory.newLatLngZoom(Position, 16));
	}
	
}

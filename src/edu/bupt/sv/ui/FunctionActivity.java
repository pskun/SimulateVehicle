package edu.bupt.sv.ui;

import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
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
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class FunctionActivity extends Activity  implements OnMapReadyCallback{
	private static final int TIME_SHORT = 0;
	private static final int LENGTH_SHORT = 1;
	
	private static final int MSG_ON_LOCATION_CHANGE = 1;
	private static final int MSG_ON_PATH_CHANGE =2;
	private static final int MSG_ON_CHARGE_CHANGE =3;
	private static final int MSG_ON_OTHERINFO_CHANGE =4;
	private static final int MSG_ON_GET_TURNNODEID =5;
	//private static final int MSG_ON_DIRECTION_CHANGE =3;
	
	private static int isChangeDes = 0;
	static final LatLng NKUT = new LatLng(23.979548, 120.696745);
    private GoogleMap map;
    private Context mContext;
    private CoreApi api;
    private int vehicleid;
    private Marker carPosition;
    private Marker start;
    private Marker end;
    private Marker turnCurrentNode;
    private Marker turnNextNode; 
    private Polyline polyline;
     
    private TextView  vid;
    private TextView  longitude;
    private TextView  latitude;
    private TextView  route;
    private TextView  battery ;
    private TextView  speed ;
    
     
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
		api.initApi();
	}
	
	private void reset() {
		api = ApiFactory.getInstance(mContext);
		api.destroyApi();
	}
	
	@Override
	public void onBackPressed() {
		reset();
		super.onBackPressed();
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
		public void onError(int errorCode) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPathChanged(boolean success, List<Point> paths,
				Point start, Point end) {
			// TODO Auto-generated method stub		
			if(success==true){
			uiHandler.obtainMessage(MSG_ON_PATH_CHANGE,paths).sendToTarget();
			uiHandler.obtainMessage(MSG_ON_LOCATION_CHANGE,start).sendToTarget();
			}else{
			LogUtil.toast(mContext, "不能向该方向转弯！");}
		}

		@Override
		public void onOtherInfoChanged(double charge, double speed,
				Integer linkId) {
			// TODO Auto-generated method stub
			
			Double linkid = Double.parseDouble(linkId.toString());
			Double[] info =new Double[]{charge,speed,linkid};
			uiHandler.obtainMessage(MSG_ON_OTHERINFO_CHANGE,info).sendToTarget();
					
		}

		@Override
		public void onGetTurnNodeId(Point crossPoint, Point newStartPoint) {
			Point[] info = new Point[]{crossPoint,newStartPoint};
			uiHandler.obtainMessage(MSG_ON_GET_TURNNODEID,info).sendToTarget();
			 
		}	
	};
	
    private OnClickListener listener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			int clickId = v.getId();
			switch(clickId)
			{
			case R.id.getcharge:
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
         
         DirectionView directionView = (DirectionView) this.findViewById(R.id.cv);
         this.mContext = this.getApplicationContext();
         vehicleid = getIntent().getIntExtra("id", 0);
         vid = (TextView) this.findViewById(R.id.vehicleid);
         latitude = (TextView) this.findViewById(R.id.latitude);
         longitude = (TextView) this.findViewById(R.id.longitude);
         
         Log.e("latitude", latitude.toString());
         
         route = (TextView) this.findViewById(R.id.route);
         battery = (TextView)this.findViewById(R.id.battery);
         speed = (TextView)this.findViewById(R.id.speed);
         
         vid.setText("车辆ID："+vehicleid);
         init();
         directionView.init(api);
         
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
                    	 switch(item.getItemId()){  
                    	 case R.id.getcharge :
                    		 handleGetCharged();
                    		 break;
                    	 case R.id.changedes :                		 
                    		 handleChangeDes();
                    		 break;
                    	 }         
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
      
      SparseArray<Node> chargeStationInfo = api.getChargeStation();
      for(int i=0;i<chargeStationInfo.size();i++){ 	 
    	  Node node = chargeStationInfo.valueAt(i);
    	  map.addMarker(new MarkerOptions().position(new LatLng(node.getLatitude(),node.getLongitude()))
    	    		  .title("STATION").snippet(String.valueOf(node.getId()))
    	    		  .icon(BitmapDescriptorFactory.fromResource(R.drawable.charge)));
      }  
     
      map.setOnMarkerClickListener(new OnMarkerClickListener() {
          @Override
          public boolean onMarkerClick(Marker arg0) {
              // TODO Auto-generated method stub
        	  //LogUtil.toast(mContext,"haha"+isChangeDes);
              if(isChangeDes == 1){
            	  LogUtil.toast(mContext, "目的地变更为:"+arg0.getPosition().latitude+" "+arg0.getPosition().longitude +"　！");
            	  api.changeDestination(Integer.parseInt(arg0.getSnippet()));         	  
              }
              isChangeDes = 0;       	  
              return true;
          }
      });
      
      LatLng NKUT = new LatLng(29.542324,-98.576859);      
      map.moveCamera(CameraUpdateFactory.newLatLngZoom(NKUT, 16));
      api.initVehicle(vehicleid);
      
	}
	
	private void handleLocalMessage(Message msg) {
		switch(msg.what) {
		case MSG_ON_PATH_CHANGE:
			handleOnPathChanged((List<Point>) msg.obj);			
			break;
		case MSG_ON_LOCATION_CHANGE:
			handleOnLocationChanged((Point) msg.obj);
			break;
		case MSG_ON_OTHERINFO_CHANGE:
			handleOnOtherinfoChanged(msg.obj);
			break;
		case MSG_ON_GET_TURNNODEID:
			handleOnGetTurnnodeid((Point[]) msg.obj);
			break;
		}
	}
	
	private void handleOnOtherinfoChanged(Object obj) {
		// TODO Auto-generated method stub
		Double[] info = (Double[]) obj;
		battery.setText("剩余电量 :"+info[0]);
		speed.setText("当前速度 ："+info[1]);	
		int temp = info[2].intValue();
		route.setText("当前路段 ："+ temp);
	}

	private void handleOnPathChanged(List<Point> paths){
		if(start!=null){
			start.remove();
		}
		if(end !=null){
			end.remove();
		}	
		if(polyline!=null){
			polyline.remove();
		}
		PolylineOptions rectOptions = new PolylineOptions();
		for(int i=0;i<paths.size();i++){
			rectOptions.add(new LatLng(paths.get(i).latitude,paths.get(i).longitude));			
		}
		polyline = map.addPolyline(rectOptions);
		start = map.addMarker(new MarkerOptions().position(new LatLng(paths.get(0).getLatitude(),paths.get(0).getLongitude()))
	    		  .icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
		end = map.addMarker(new MarkerOptions().position(new LatLng(paths.get(paths.size()-1).getLatitude(),paths.get(paths.size()-1).getLongitude()))
	    		  .icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));
		
	}
	
	private void handleOnLocationChanged(Point newPoint){
		latitude.setText("当前经度： " + newPoint.longitude);
		longitude.setText("当前纬度：" + newPoint.latitude);
		LogUtil.error("onLocationChange:" + newPoint.latitude + " " + newPoint.longitude);
		if(carPosition!=null)
			carPosition.remove();
		LatLng Position = new LatLng(newPoint.latitude, newPoint.longitude);  
		carPosition= map.addMarker(new MarkerOptions().position(Position)); 
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(Position,16));
	}
	
	private void handleOnGetTurnnodeid(Point[] points){
		//System.out.println("turn+node#########"+points[0]);
		if(turnCurrentNode!=null){
			turnCurrentNode.remove();
		}
		if(turnNextNode!=null){
			turnNextNode.remove();
		}
		turnCurrentNode = map.addMarker(new MarkerOptions().position(new LatLng(points[0].getLatitude(),points[0].getLongitude()))
	    		  .icon(BitmapDescriptorFactory.fromResource(R.drawable.attention)));
		turnNextNode = map.addMarker(new MarkerOptions().position(new LatLng(points[1].getLatitude(),points[1].getLongitude()))
	    		  .icon(BitmapDescriptorFactory.fromResource(R.drawable.attention)));
		
	}

	private void handleChangeDes(){		
		isChangeDes = 1;	
	}
	
	private void handleGetCharged(){
		api.requestCharge();
	}
	
	private int  returnStrategy(){
		RadioButton timeShortButton = (RadioButton) this.findViewById(R.id.radioMale);
		if(timeShortButton.isChecked()){
			return TIME_SHORT;
		}else
			return LENGTH_SHORT;
	}
}

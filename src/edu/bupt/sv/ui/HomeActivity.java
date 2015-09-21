package edu.bupt.sv.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.bupt.sv.core.ApiFactory;
import edu.bupt.sv.core.CoreApi;
import edu.bupt.sv.entity.Vehicle;
import edu.bupt.sv.utils.ConfigUtil;
import edu.bupt.sv.utils.LogUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class HomeActivity extends Activity {
	private ListView vehicleList;
	private Button confirmButton;
	private Button allButton;
	private EditText vid;

	
	private Context mContext;
	//private Handler myHandler;
	private CoreApi api;
	
	private void init() {
		api = ApiFactory.getInstance(mContext);
		//api.initApi();
	}
	
	private OnItemClickListener  listener = new OnItemClickListener()  {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			clickList(position);
             		
		}
	};
	
	private OnClickListener clistener = new OnClickListener(){
		public void onClick(View v){
			int buttonid = v.getId();
			switch (buttonid){
			case R.id.confirmbutton:
				if(!TextUtils.isEmpty(vid.getText())){
				int getVid =Integer.parseInt(vid.getText().toString());
				if(0<= getVid && getVid<=199){
					  Intent intent = new Intent();
			    	  intent.setClassName(mContext, "edu.bupt.sv.ui.FunctionActivity");
			    	  System.out.println("###########vid+"+getVid);   	  
			    	  intent.putExtra("id", getVid);
			    	  startActivity(intent);
				}}
				else
					LogUtil.toast(mContext, "请输入有效的车辆ID！");	
				
				break;
			case R.id.allbutton:
				loadInfo(api.getVehicleList());
				vehicleList.setVisibility(View.VISIBLE);
				System.out.println("###########beforebreak+");   	 
				break;
			case R.id.home_page:
				 InputMethodManager imm = (InputMethodManager)  
		         getSystemService(Context.INPUT_METHOD_SERVICE);  
		         imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
		        break;  		
				
			}
		}
	};
		
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.home_page);
		this.mContext = this.getApplicationContext();
		
		confirmButton = (Button) findViewById(R.id.confirmbutton);
		confirmButton.setOnClickListener(clistener);
		vid = (EditText) findViewById(R.id.vidinput);
		
		allButton=(Button) findViewById(R.id.allbutton);
		allButton.setOnClickListener(clistener);	
		
		findViewById(R.id.home_page).setOnClickListener(clistener);
		init();	
		//loadInfo(api.getVehicleList());
	}
	
	private void loadInfo (SparseArray<Vehicle> vehicles) {

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
		System.out.println("###########beforeexit+");   	
		
	}
	
	public void clickList (int position){
		//if (isNetworkAvailable(HomeActivity.this))
		//{
		  Intent intent = new Intent();
    	  intent.setClassName(this, "edu.bupt.sv.ui.FunctionActivity");
    	  intent.putExtra("id", position);
    	  startActivity(intent);
    	//}
		//else {
			//LogUtil.toast(mContext, "无可用网络！");
		//}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
    /**
     * 检查当前网络是否可用
     * 
     * @param context
     * @return
     */
    
   /* public boolean isNetworkAvailable(Activity activity)
    {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            
            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }*/
	

}

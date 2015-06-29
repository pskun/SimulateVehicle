package edu.bupt.sv.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.bupt.sv.core.ApiFactory;
import edu.bupt.sv.core.CoreApi;
import edu.bupt.sv.entity.Vehicle;
import edu.bupt.sv.utils.LogUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class HomeActivity extends Activity {
	private ListView vehicleList;
	
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
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.home_page);
		
		this.mContext = this.getApplicationContext();
		init();	
		loadInfo(api.getVehicleList());
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
		
	}
	
	public void clickList (int position){
		if (isNetworkAvailable(HomeActivity.this))
		{
		  Intent intent = new Intent();
    	  intent.setClassName(this, "edu.bupt.sv.ui.FunctionActivity");
    	  intent.putExtra("id", position);
    	  startActivity(intent);
    	}
		else {
			LogUtil.toast(mContext, "无可用网络！");
		}
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
    
    public boolean isNetworkAvailable(Activity activity)
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
    }
	

}

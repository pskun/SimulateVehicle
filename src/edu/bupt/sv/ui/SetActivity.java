package edu.bupt.sv.ui;

import java.util.List;

import edu.bupt.sv.core.ApiFactory;
import edu.bupt.sv.core.CoreApi;
import edu.bupt.sv.core.CoreListener;
import edu.bupt.sv.entity.Point;
import edu.bupt.sv.utils.LogUtil;
import edu.bupt.sv.utils.ConfigUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetActivity extends Activity {
	private Context ctx = SetActivity.this;
	private Button startButton;
	private EditText tmHost;
	private EditText tmPort;
	private EditText ivoHost;
	private CoreApi api;
	private static final int MSG_ON_RECEIVE_STAT = 1;
	
	private OnClickListener listener = new OnClickListener(){
		public void onClick(View v){
			int buttonid = v.getId();
			switch (buttonid){
			case R.id.startbutton:
				ConfigUtil.writeTmHost(ctx,tmHost.getText().toString());
				ConfigUtil.writeTmPort(ctx,tmPort.getText().toString());
				ConfigUtil.writeIovAddr(ctx,ivoHost.getText().toString());
				//checkInternet
				System.out.println("########InittmHost"+ConfigUtil.readTmHost(ctx));
				if (!isNetworkAvailable(SetActivity.this)){
					LogUtil.toast(SetActivity.this, "无可用网络！");	
					break;}
				reset();
				init();
				break;
			}
		}
	};
		
	private CoreListener coreListener = new CoreListener() {
		
		@Override
		public void onRecvVehicleList(List<Integer> vehicleIds) {
		}
		@Override
		public void onLocationChanged(Point newPoint) {
		}
		@Override
		public void onError(int errorCode) {		
		}		
		@Override
		public void onPathChanged(boolean success, List<Point> paths,
				Point start, Point end) {	
		}
		@Override
		public void onOtherInfoChanged(double charge, double speed,Integer linkId,
				Integer status) {			
		}
		@Override
		public void onGetTurnNodeId(Point crossPoint, Point newStartPoint) {
		}
		
		
		@Override
		public void onInitStatus(int status) {
			// TODO Auto-generated method stub	
			//checkConnect
			if(status==-1)
				reset();
			uiHandler.obtainMessage(MSG_ON_RECEIVE_STAT,status).sendToTarget();		
	}
	};

	private Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			handleLocalMessage(msg);
		}
	};
	
	private void handleLocalMessage(Message msg) {
		switch(msg.what) {
		case MSG_ON_RECEIVE_STAT:
			handleOnReceiveStat(msg.obj);
			break;
		}
	}
	private void handleOnReceiveStat(Object obj) {
		int stat = ((Integer)obj).intValue();	
		if(stat == -1){
			System.out.println("###########before reset!");	
			reset();
			new AlertDialog.Builder(SetActivity.this).setTitle("提示信息").setMessage("车联网服务或TM服务不可用！")
			.setPositiveButton("确定", null).setNegativeButton("取消",null)
			.show();
		}else{				
			Intent intent = new Intent();
		    intent.setClassName(SetActivity.this, "edu.bupt.sv.ui.HomeActivity");
		    startActivity(intent);
		}
	}
	private void init() {
		api = ApiFactory.getInstance(ctx);
		api.setListener(coreListener);
		api.initApi();
	}	
	private void reset() {
		api = ApiFactory.getInstance(ctx);
		api.destroyApi();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 
		setContentView(R.layout.set_page);
		startButton = (Button) findViewById(R.id.startbutton);
		startButton.setOnClickListener(listener);
		
		tmHost = (EditText) findViewById(R.id.tmhost);
		tmPort = (EditText) findViewById(R.id.tmport) ;
		ivoHost = (EditText) findViewById(R.id.ivohost) ;
		//System.out.println("11"+ConfigUtil.readTmHost(ctx));
		tmHost.setText(ConfigUtil.readTmHost(ctx));
		tmPort.setText(String.valueOf(ConfigUtil.readTmPort(ctx)));
		ivoHost.setText(ConfigUtil.readIovAddr(ctx));
		
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

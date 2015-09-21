package edu.bupt.sv.ui;

import edu.bupt.sv.core.ApiFactory;
import edu.bupt.sv.core.CoreApi;
import edu.bupt.sv.core.MsgConstants;
import edu.bupt.sv.service.CheckStateListener;
import edu.bupt.sv.utils.CommonUtil;
import edu.bupt.sv.utils.LogUtil;
import edu.bupt.sv.utils.ConfigUtil;
import android.app.Activity;
import android.app.AlertDialog;

import android.app.ProgressDialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class SetActivity extends Activity implements MsgConstants {
	
	private Context ctx = null;
	
	private Button startButton;

	private ProgressDialog progressbar;

	private EditText tmHostText;
	private EditText tmPortText;
	private EditText iovHostText;
	private CoreApi api = null;

	private static final int MSG_ON_RECEIVE_STAT = 1;
	
	private OnClickListener listener = new OnClickListener(){
		public void onClick(View v){
			int buttonid = v.getId();
			switch (buttonid){
			case R.id.startbutton:
				ConfigUtil.writeTmHost(ctx, tmHostText.getText().toString());
				ConfigUtil.writeTmPort(ctx, tmPortText.getText().toString());
				ConfigUtil.writeIovAddr(ctx, iovHostText.getText().toString());
				//checkInternet
				// System.out.println("########InittmHost"+ConfigUtil.readTmHost(ctx));
				if (!CommonUtil.isNetworkAvailable(SetActivity.this)){
					LogUtil.toast(SetActivity.this, "无可用网络！");	
					break;}
				reset();
				init();
				progressbar = ProgressDialog.show(ctx, "提示", "正在连接中...");  
				break;
			}
		}
	};
		
	private CheckStateListener stateListener = new CheckStateListener() {	
		@Override
		public void onInitStatus(int status) {
			//checkConnect
			if(status == INIT_STATUS_FAILED)
				reset();
			uiHandler.obtainMessage(MSG_ON_RECEIVE_STAT, status).sendToTarget();		
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
		progressbar.dismiss();
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
		if(api!=null) {
			api.setStateListener(stateListener);
			api.initApi();
		}
	}
	
	private void reset() {
		if(api!=null)
			api.destroyApi();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 
		ctx = SetActivity.this;
		
		setContentView(R.layout.set_page);
		startButton = (Button) findViewById(R.id.startbutton);
		startButton.setOnClickListener(listener);
		
		tmHostText = (EditText) findViewById(R.id.tmhost);
		tmPortText = (EditText) findViewById(R.id.tmport) ;
		iovHostText = (EditText) findViewById(R.id.ivohost) ;
		
		tmHostText.setText(ConfigUtil.readTmHost(ctx));
		tmPortText.setText(String.valueOf(ConfigUtil.readTmPort(ctx)));
		iovHostText.setText(ConfigUtil.readIovAddr(ctx));
		
		api = ApiFactory.getInstance(ctx);
	}

}

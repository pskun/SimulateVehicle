package edu.bupt.sv.ui;

import edu.bupt.sv.utils.LogUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SetActivity extends Activity {
	private Button startButton;
	private OnClickListener listener = new OnClickListener(){
		public void onClick(View v){
			int buttonid = v.getId();
			switch (buttonid){
			case R.id.startbutton:
				//checkInternet
				if (!isNetworkAvailable(SetActivity.this))
					LogUtil.toast(SetActivity.this, "无可用网络！");				
				//checkConnect
				else if(!isNetworkAvailable(SetActivity.this)){
					
				}else{
				final EditText texta = new EditText(SetActivity.this);
				new AlertDialog.Builder(SetActivity.this).setTitle("请输入需要模拟的车辆ID").setView
				   (texta).setPositiveButton("确定",new DialogInterface.OnClickListener(){
					   @Override
					   public void onClick(DialogInterface dialog, int which){
							  Intent intent = new Intent();
					    	  intent.setClassName(SetActivity.this, "edu.bupt.sv.ui.FunctionActivity");
					    	  intent.putExtra("id", texta.getText());
					    	  startActivity(intent);						   
					   }				   
				   }).setNeutralButton("显示车辆列表",new DialogInterface.OnClickListener(){
					   @Override
					   public void onClick(DialogInterface dialog, int which){
						   Intent intent = new Intent();
					       intent.setClassName(SetActivity.this, "edu.bupt.sv.ui.HomeActivity");
					       startActivity(intent);
					   }
				   }).setNegativeButton("取消",new DialogInterface.OnClickListener(){
					   @Override
					   public void onClick(DialogInterface dialog, int which){
						   
					   }
				   }).show();}
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.set_page);
		startButton = (Button) findViewById(R.id.startbutton);
		startButton.setOnClickListener(listener);	
		
		
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

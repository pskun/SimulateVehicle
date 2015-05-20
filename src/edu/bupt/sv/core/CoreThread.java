package edu.bupt.sv.core;

import java.util.List;

import edu.bupt.sv.control.VehicleController;
import edu.bupt.sv.entity.Point;
import edu.bupt.sv.entity.SubInfo;
import edu.bupt.sv.service.TMAccessor;
import edu.bupt.sv.utils.DataConfig;
import edu.bupt.sv.utils.LogUtil;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class CoreThread implements Runnable, MsgConstants, ErrorConstants {

	private boolean isRunning = false;
	private CoreListener coreListener = null;
	private Handler mHandler;
	private Context mContext;
	
	private VehicleController vehicleController;
	private TMAccessor tmAccessor;
	private DataConfig dataConfig;
	
	public CoreThread(Context context) {
		super();
		this.mContext = context;
	}

	public boolean isThreadRunning() {
		return isRunning;
	}

	public void setListener(CoreListener listener) {
		this.coreListener = listener;
	}
	
	public void sendMessage(int msgCode) {
		if(mHandler == null) {
			LogUtil.warn("coreThread handler is null. Unable to send message.");
			return;
		}
		mHandler.obtainMessage(msgCode).sendToTarget();
	}
	
	public void sendMessage(int msgCode, Object object) {
		if(mHandler == null) {
			LogUtil.warn("coreThread handler is null. Unable to send message.");
			return;
		}
		mHandler.obtainMessage(msgCode, object).sendToTarget();
	}
	
	public void destroy() {
		if(mHandler == null) {
			LogUtil.warn("coreThread handler is null. Don't need to destroy.");
			return;
		}
		mHandler.removeCallbacksAndMessages(null);
		mHandler.obtainMessage(MSG_ON_QUIT).sendToTarget();
	}
	
	@Override
	public void run() {
		isRunning = true;
        Looper.prepare();

        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handleLocalMessage(msg);
            }
        };
        
        sendMessage(MSG_INIT_THREAD);
        Looper.loop();
	}

	private void handleLocalMessage(Message msg) {
		if(!isRunning)
			return;
		switch(msg.what)
		{
		case MSG_INIT_THREAD:
			handleInitThread();
			break;
		case MSG_VEHICLE_LIST:
			handleRequestVehicleList();
			break;
		case MSG_INIT_VEHICLE:
			handleInitVehicle((Integer) msg.obj);
			break;
		case MSG_ON_RECEIVE:
			handleReceiveData(msg.arg1, msg.obj);
			break;
		case MSG_ON_QUIT:
			handleOnQuit();
			break;
		}
	}
	
	private void handleInitThread() {
		LogUtil.verbose("coreThread: begin initialize thread.");
		if (null == vehicleController) {
			vehicleController = new VehicleController();
		}
		else {
			LogUtil.warn("vehicleController already exists.");
		}
		if (null == tmAccessor) {
			tmAccessor = new TMAccessor(mHandler);
			tmAccessor.init();
		} else {
			LogUtil.warn("tmAccessor already exists");
		}
		if (null == dataConfig) {
			dataConfig = new DataConfig(mContext);
			if(!dataConfig.initAll()) {
				mHandler.obtainMessage(MSG_ON_ERROR).sendToTarget();
				return;
			}
			if (null != coreListener) {
				coreListener.onInitFinish(dataConfig.getNodeInfo(), dataConfig.getVehicleList());
			}
		}
		LogUtil.verbose("coreThread is now initialized.");
	}
	
	private void handleOnQuit() {
		LogUtil.verbose("coreThread: begin destroy thread.");
		isRunning = false;
		if(tmAccessor != null) {
			tmAccessor.destroy();
		}
		// TODO
		Looper.myLooper().quit();
		LogUtil.verbose("coreThread is now destroyed.");
	}
	
	private void handleRequestVehicleList() {
		if (tmAccessor != null) {
			boolean ret = tmAccessor.requestAllVehicle();
			if(!ret && coreListener != null) {
				coreListener.onError(ERROR_REQUEST_VEHICLE_LIST);
			}
		}
	}
	
	private void handleInitVehicle(Integer vehicleId) {
		// 从vehicleList中选一个
		// TODO
		if(tmAccessor != null) {
			boolean ret = tmAccessor.requestInitVehicle(vehicleId);
			if(!ret && coreListener != null) {
				coreListener.onError(ERROR_INIT_VEHICLE);
			}
		}
	}
	
	private void onReceiveSubInfoData(SubInfo subInfo) {
		if (vehicleController == null) {
			LogUtil.warn("coreThread: vehicleController is null");
			return;
		}
		if (vehicleController.setLocation(subInfo.latitude, subInfo.longitude)) {
			coreListener.onLocationChanged(new Point(subInfo.latitude, subInfo.longitude));
		}
		if (vehicleController.setCharge(subInfo.currentCharge)) {
			coreListener.onChargedChanged(subInfo.currentCharge);
		}
	}
	
	private void handleReceiveData(int dataType, Object data) {
		if(data == null) {
			if(coreListener != null) {
				coreListener.onError(ERROR_EMPTY_DATA);
			}
		}
		if(coreListener == null) {
			LogUtil.error("CoreThread: coreListener is null.");
			return;
		}
		switch(dataType)
		{
		case DATA_VEHICLE_LIST:
			coreListener.onRecvVehicleList((List<Integer>) data);
			break;
		case DATA_VEHICLE_INFO:
			onReceiveSubInfoData((SubInfo) data);
			break;
		case DATA_PATH_PLAN:
			break;
		}
	}
}

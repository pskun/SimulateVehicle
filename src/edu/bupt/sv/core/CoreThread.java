package edu.bupt.sv.core;

import java.util.List;

import edu.bupt.sv.control.VehicleController;
import edu.bupt.sv.service.TMAccessor;
import edu.bupt.sv.utils.LogUtil;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class CoreThread implements Runnable, MsgConstants, ErrorConstants {

	private boolean isRunning = false;
	private CoreListener coreListener = null;
	private Handler mHandler;
	
	private VehicleController vehicleController;
	private TMAccessor tmAccessor;
	
	public CoreThread() {
		super();
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
		case MSG_ON_RECEIVE:
			handleReceiveData(msg.arg1, msg.obj);
			break;
		}
	}
	
	private void handleInitThread() {
		if (null != vehicleController) {
			vehicleController = new VehicleController();
		}
		else {
			LogUtil.warn("vehicleController already exists.");
		}
		if (null != tmAccessor) {
			tmAccessor = new TMAccessor(mHandler);
			tmAccessor.init();
		}
	}
	
	private void handleRequestVehicleList() {
		
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
		}
	}
}

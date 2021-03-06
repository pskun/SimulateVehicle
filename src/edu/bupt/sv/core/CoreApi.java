package edu.bupt.sv.core;

import android.content.Context;
import android.util.SparseArray;
import edu.bupt.sv.entity.Node;
import edu.bupt.sv.entity.Vehicle;
import edu.bupt.sv.service.CheckStateListener;
import edu.bupt.sv.utils.DataConfig;
import edu.bupt.sv.utils.LogUtil;

public class CoreApi implements CoreInterface, MsgConstants, ErrorConstants {

	private CoreThread coreThread = null;
	private CoreListener coreListener = null;
	
	private CheckStateListener stateListener = null;
	
	private Context mContext;
	
	public CoreApi(Context mContext) {
		super();
		this.mContext = mContext;
	}

	public void setListener(CoreListener listener) {
		this.coreListener = listener;
	}
	
	
	public void setStateListener(CheckStateListener stateListener) {
		this.stateListener = stateListener;
	}
	
	public void removeStateListener() {
		stateListener = null;
	}
	
	/**
	 * coreThread线程是否正常
	 * @return
	 */
	private boolean isThreadValid() {
		if(coreThread == null || !coreThread.isThreadRunning())
		{
			LogUtil.error("coreThread doesn't exists.");
			if(coreListener != null)
				coreListener.onError(ERROR_THREAD_FATAL);
			return false;
		}
		return true;
	}
	
	@Override
	public boolean initApi() {
		if(coreThread != null) {
			LogUtil.warn("coreThread already exists.");
			coreThread.setListener(coreListener);
			coreThread.setStateListener(stateListener);
			return true;
		}
		coreThread = new CoreThread(mContext);
		coreThread.setListener(coreListener);
		coreThread.setStateListener(stateListener);

		try {
			new Thread(coreThread).start();
		} catch(IllegalThreadStateException e) {
			e.printStackTrace();
			return false;
		}
		LogUtil.verbose("coreThread started.");
		return true;
	}
	
	@Override
	public boolean initVehicle(Integer vehicleId) {
		if(!isThreadValid()){
			return false;
		}
		coreThread.sendMessage(MSG_INIT_VEHICLE, vehicleId);
		return true;
	}

	@Override
	public boolean turnNewPath(int direction) {
		if(!isThreadValid())
			return false;
		coreThread.sendMessage(MSG_CHANGE_PATH, Integer.valueOf(direction));
		return true;
	}

	@Override
	public boolean changeDestination(Integer newDestNodeId) {
		if(!isThreadValid())
			return false;
		coreThread.sendMessage(MSG_CHANGE_DEST, newDestNodeId);
		return true;
		
	}
	
	public int getCurrentState(){
		return 0;
	}

	@Override
 	public void requestCharge() {
		if(!isThreadValid())
			return;
		coreThread.sendMessage(MSG_REQUEST_CHARGE);
	}

	@Override
	public void requestVehicleList() {
		if(isThreadValid())
			coreThread.sendMessage(MSG_VEHICLE_LIST);
	}

	@Override
	public void destroyApi() {
		if(isThreadValid()) {
			coreThread.destroy();
			coreListener = null;
		}
		coreThread = null;
	}

	@Override
	public SparseArray<Vehicle> getVehicleList() {
		DataConfig dc = DataConfig.getInstance(mContext);
		return dc.getVehicleList();
	}

	@Override
	public SparseArray<Node> getNodeList() {
		DataConfig dc = DataConfig.getInstance(mContext);
		return dc.getNodeList();
	}

	@Override
	public SparseArray<Node> getChargeStation() {
		DataConfig dc = DataConfig.getInstance(mContext);
		return dc.getStationList();
	}
	
}

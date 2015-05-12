package edu.bupt.sv.core;

import edu.bupt.sv.utils.LogUtil;

public class CoreApi implements CoreInterface, MsgConstants, ErrorConstants {

	private CoreThread coreThread = null;
	private CoreListener coreListener = null;
	
	public void setListener(CoreListener listener) {
		this.coreListener = listener;
	}
	
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
			return true;
		}
		coreThread = new CoreThread();
		coreThread.setListener(coreListener);
		try {
			new Thread(coreThread).start();
		} catch(IllegalThreadStateException e) {
			e.printStackTrace();
		}
		LogUtil.verbose("coreThread started.");
		return true;
	}
	
	@Override
	public boolean initVehicle() {
		return false;
	}

	@Override
	public boolean turnNewPath() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void changeDestination() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestCharge() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestVehicleList() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getVehicleList() {
		if(isThreadValid())
			coreThread.sendMessage(MSG_VEHICLE_LIST);
		return true;
	}

}

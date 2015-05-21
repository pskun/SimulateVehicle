package edu.bupt.sv.core;

import java.util.List;

import edu.bupt.sv.entity.Point;
import edu.bupt.sv.entity.SubInfo;
import edu.bupt.sv.entity.Vehicle;
import edu.bupt.sv.service.PathPlanTask;
import edu.bupt.sv.service.TMAccessor;
import edu.bupt.sv.utils.CommonUtil;
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
	
	private TMAccessor tmAccessor;
	private DataConfig dataConfig;
	
	private Vehicle vehicle;
	
	// �������
	private PathPlanTask ppTask;
	
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
        System.out.println("44444444");
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
		case MSG_PATH_PLAN:
			handlePathPlan((Integer) msg.obj);
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
		vehicle = null;
		if (null == tmAccessor) {
			tmAccessor = new TMAccessor(mHandler);
			tmAccessor.init();
		} else {
			LogUtil.warn("tmAccessor already exists.");
		}
		System.out.println("7888888");
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
		if (ppTask == null) {
			ppTask = new PathPlanTask(mHandler, tmAccessor);
		} else {
			LogUtil.warn("ppTask already exists.");
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
	
	/**
	 * ��ʼ����������TM���ĳ�����Ϣ
	 * @param vehicleId
	 */
	private void handleInitVehicle(Integer vehicleId) {
		// ��vehicleList��ѡһ��
		Vehicle v = dataConfig.getVehicleFromConfig(vehicleId);
		this.vehicle = v;
		// ���ĳ�����Ϣ
		boolean ret = tmAccessor.requestInitVehicle(vehicleId);
		if(!ret && coreListener != null) {
			coreListener.onError(ERROR_INIT_VEHICLE);
		}
	}
	
	/**
	 * �յ�ת�����󣬿�ʼ·���滮
	 * @param direction
	 */
	private void handlePathPlan(Integer direction) {
		// ��õ�ǰ��linkid
		Integer currentLinkId = vehicle.getLinkID();
		// ��ù滮��·������һ��link
		Integer nextLinkId = vehicle.getNextLinkOfPath(currentLinkId);
		// ��ȡ����ת�����һ��link
		Integer turnLinkId = dataConfig.getTurnLink(currentLinkId, direction.intValue());
		// ����ת�������·���滮��ͬ
		if(nextLinkId==null || turnLinkId==null || nextLinkId.equals(turnLinkId)) {
			coreListener.onPathChanged(false, null);
			LogUtil.verbose("turn new path failed.");
			String hint = CommonUtil.catString("currentLinkId: ", currentLinkId, "nextLink: ", nextLinkId, "turnLinkId: ",turnLinkId);
			LogUtil.verbose(hint);
			return;
		}
	}
	
	private void onReceiveSubInfoData(SubInfo subInfo) {
		// just for debug
		// LogUtil.warn("Current link: " + subInfo.linkId);
		// LogUtil.warn("SubInfo: " + subInfo.latitude + " " + subInfo.longitude);
		// ��ǰlinkid
		setCurrentLink(subInfo.linkId);
		// ��ǰλ��
		if (setLocation(subInfo.latitude, subInfo.longitude)) {
			coreListener.onLocationChanged(new Point(subInfo.latitude, subInfo.longitude));
		}
		// ��ǰ����
		if (setCharge(subInfo.currentCharge)) {
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
	
	/**
	 * �����ǹ��ڳ�����Ϣ�ĺ���
	 */
	private boolean isExistVehicle() {
		if(vehicle == null) {
			LogUtil.error("entity vehicle is null.");
			coreListener.onError(ERROR_NULL_POINTER);
			return false;
		}
		return true;
	}
	/**
	 * ����λ�ã����λ���и����򷵻�true�����򷵻�false
	 * @param latitude
	 * @param longitude
	 */
	private boolean setLocation(double latitude, double longitude) {
		if(!isExistVehicle())
			return false;
		if(vehicle.getLatitude() != latitude || vehicle.getLongitude() != longitude) {
			vehicle.setLatitude(latitude);
			vehicle.setLongitude(longitude);
			return true;
		}
		return false;
	}
	
	private boolean setCharge(double charge){
		if(!isExistVehicle())
			return false;
		if(vehicle.getCharge() != charge) {
			vehicle.setCharge(charge);
			return true;
		}
		return false;
	}
	
	private boolean setCurrentLink(int linkid) {
		if(!isExistVehicle())
			return false;
		if(vehicle.getLinkID().intValue() != linkid) {
			vehicle.setLinkID(Integer.valueOf(linkid));
			return true;
		}
		return false;
	}
}

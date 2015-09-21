package edu.bupt.sv.core;

import java.util.List;

import junit.framework.Assert;

import edu.bupt.sv.entity.PathInfo;
import edu.bupt.sv.entity.Point;
import edu.bupt.sv.entity.SubInfo;
import edu.bupt.sv.entity.Vehicle;
import edu.bupt.sv.service.CheckStateListener;
import edu.bupt.sv.service.PathPlanTask;
import edu.bupt.sv.service.TMAccessor;
import edu.bupt.sv.utils.CommonUtil;
import edu.bupt.sv.utils.ConfigUtil;
import edu.bupt.sv.utils.DataConfig;
import edu.bupt.sv.utils.LogUtil;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public final class CoreThread implements Runnable, MsgConstants, ErrorConstants {

	private static final String TAG = "coreThread";
	private boolean isRunning = false;
	
	private CoreListener coreListener = null;
	private CheckStateListener stateListener = null;
	
	private Handler mHandler;
	private Context mContext;
	
	private TMAccessor tmAccessor;
	private DataConfig dataConfig;
	
	private Vehicle vehicle;
	// ��ʱ�Ķ��󣬱���ת��͸ı��յ�ʱ����ʱlink
	private Integer tempLinkId;
	
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
	
	public void setStateListener(CheckStateListener stateListener) {
		this.stateListener = stateListener;
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
		case MSG_CHANGE_PATH:
			handlePathPlan((Integer) msg.obj);
			break;
		case MSG_CHANGE_DEST:
			handleChangeDest((Integer) msg.obj);
			break;
		case MSG_REQUEST_CHARGE:
			handleRequestCharge();
			break;
		case MSG_ON_RECEIVE:
			handleReceiveData(msg.arg1, msg.obj);
			break;
		case MSG_ON_ERROR:
			handleOnError(msg.arg1, msg.obj);
			break;
		case MSG_ON_QUIT:
			handleOnQuit();
			break;
		}
	}
	
	/**
	 * �յ�Init��Ϣ��Ĵ�����
	 */
	private void handleInitThread() {
		LogUtil.verbose("coreThread: begin initialize thread.");
		// ��ȡ�����ļ�
		String tmHost = ConfigUtil.readTmHost(mContext);
		int tmPort = ConfigUtil.readTmPort(mContext);
		// ip�Ͷ˿ڲ��Ϸ�
		if(tmHost==null || tmPort<0) {
			LogUtil.error("ip�Ͷ˿ڲ��Ϸ�");
			mHandler.obtainMessage(MSG_ON_ERROR, ERROR_ON_INIT, -1).sendToTarget();
			return;
		}
		
		
		// ��ʼ����working�߳�
		vehicle = null;

		tmAccessor = new TMAccessor(mHandler);
		// ��ʼ��TMʧ����
		if(!tmAccessor.init(tmHost, tmPort)) {
			mHandler.obtainMessage(MSG_ON_ERROR, ERROR_ON_INIT, -1).sendToTarget();
			return;
		}

		dataConfig = DataConfig.getInstance(mContext);

		ppTask = new PathPlanTask(mContext, mHandler, tmAccessor);

		LogUtil.verbose("coreThread is now initialized.");
		
		// �ص���ʼ���ɹ��ӿ�
		if(stateListener != null) {
			stateListener.onInitStatus(INIT_STATUS_OK);
		}
	}
	
	/**
	 * �յ�quit��Ϣ��Ĵ�����
	 */
	private void handleOnQuit() {
		LogUtil.verbose("coreThread: begin destroy thread.");
		isRunning = false;
		if(tmAccessor != null) {
			tmAccessor.destroy();
			tmAccessor = null;
		}
		if(ppTask != null) {
			ppTask.destroy();
			ppTask = null;
		}
		// ������Ϊ��
		this.vehicle = null;
		Looper.myLooper().quit();
		LogUtil.verbose("coreThread is now destroyed.");
	}
	
	/**
	 * @deprecated
	 * �յ��������б��Ĵ�����
	 */
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
		Assert.assertNotNull(v);
		this.vehicle = v;
		// ��ʱ��ǰlinkidΪ0,�ֶ�����
		vehicle.setLinkID(vehicle.getPath().get(0)); 
		if(coreListener!=null) {
			coreListener.onOtherInfoChanged(vehicle.getCharge(), vehicle.getSpeed().doubleValue(), vehicle.getLinkID(),vehicle.getStatus());
		}
		// �������ĳ�ʼ����Ϣ
		List<Integer> pathLinks = vehicle.getPath();
		List<Point> points = dataConfig.getPointsOfLink(pathLinks);
		Integer startNodeID = vehicle.getStartPos();
		Integer endNodeId = vehicle.getEndPos();
		Point startPoint = dataConfig.getLatLngOfNode(startNodeID);
		Point endPoint = dataConfig.getLatLngOfNode(endNodeId);
		points.add(0, startPoint);
		points.add(endPoint);
		if(coreListener != null) {
			coreListener.onPathChanged(true, points, startPoint, endPoint);
		}
		// ���ĳ�����Ϣ
		boolean ret = tmAccessor.requestInitVehicle(vehicleId);
		if(!ret && coreListener != null) {
			mHandler.obtainMessage(MSG_ON_ERROR, ERROR_INIT_VEHICLE, -1).sendToTarget();
		}
	}
	
	/**
	 * �յ�ת�����󣬿�ʼ·���滮
	 * @param direction
	 */
	private void handlePathPlan(Integer direction) {
		// ���ڳ��ʱ���ܸı�·����TM�ᱨbug
		if(isNowCharging()) {
			LogUtil.toast(mContext, "���״̬���ܸı�·����");
			return;
		}		
		LogUtil.verbose("coreThread: start path plan. direction: " + direction);
		// ��õ�ǰ��linkid
		Integer currentLinkId = vehicle.getLinkID();
		if(!CommonUtil.isLinkNodeIdValid(currentLinkId)) {
			LogUtil.warn("current link id " + currentLinkId + " is invalid.");
			return;
		}
		// ��ù滮��·������һ��link
		Integer nextLinkId = vehicle.getNextLinkOfPath(currentLinkId);
		// ��ȡ����ת�����һ��link
		Integer turnLinkId = dataConfig.getTurnLink(currentLinkId, direction.intValue());
		// ת��������Ż�
		if(!CommonUtil.isLinkNodeIdValid(turnLinkId)) {
			// ����滮·������һ��link���Ⱥ�С(С��50m��)��˵����·��֮������ӵĶ�id
			// ��ô���ж�һ��nextLink�Ƿ����ת��
			if(CommonUtil.isLinkNodeIdValid(nextLinkId)) {
				double nextLength = dataConfig.getLinkLength(nextLinkId);
				if(nextLength<=50) {
					turnLinkId = dataConfig.getTurnLink(nextLinkId, direction.intValue());
				}
			}
		}
		// ����ת�������·���滮��ͬ
		if(!CommonUtil.isLinkNodeIdValid(nextLinkId)
				|| !CommonUtil.isLinkNodeIdValid(turnLinkId)
				|| nextLinkId.intValue() == turnLinkId.intValue()) {	
			double turnLength = dataConfig.getLinkLength(turnLinkId);
			coreListener.onPathChanged(false, null, null, null);
			LogUtil.verbose("turn new path failed.");
			String hint = CommonUtil.catString("currentLinkId: ", currentLinkId, " nextLink: ", nextLinkId, " turnLinkId: ",turnLinkId);
			LogUtil.verbose(hint);
			Log.e("Link Length", "turn length: " + turnLength);
			return;
		}
		LogUtil.verbose("next link length: " + dataConfig.getLinkLength(nextLinkId));
		Integer tempDestNodeId = dataConfig.getEndNodeIdOfLink(turnLinkId);
		Point startPoint = dataConfig.getEndPointOfLink(turnLinkId);
		Integer endNodeId = vehicle.getEndPos();
		Point endPoint = dataConfig.getLatLngOfNode(endNodeId);
		tempLinkId = turnLinkId;
		// just for debug
		coreListener.onGetTurnNodeId(dataConfig.getStartPointOfLink(turnLinkId), startPoint);
		ppTask.startTask(vehicle.getId(), startPoint, endPoint, tempDestNodeId);
	}
	
	/**
	 * �յ��ı��յ����󣬿�ʼ·���滮
	 * @param newDestNodeId
	 */
	private void handleChangeDest(Integer newDestNodeId) {
		if(isNowCharging()) return;
		LogUtil.verbose("coreThread: start change destination. newDestNodeId: " + newDestNodeId);
		// ��õ�ǰ��linkid
		Integer currentLinkId = vehicle.getLinkID();
		// ��ù滮��·������һ��link
		Integer nextLinkId = vehicle.getNextLinkOfPath(currentLinkId);
		// ��ù滮��·�����յ�
		Integer destNodeId = vehicle.getEndPos();
		// �Ѿ���ʻ�����һ��link����
		if(nextLinkId==null) {
			// �ж����յ㻹�ж�Զ
			Point endPoint = dataConfig.getEndPointOfLink(currentLinkId);
			double dis = CommonUtil.getDistance(vehicle.getLatitude(), vehicle.getLongitude(), endPoint.latitude, endPoint.longitude);
			if(dis>=50) {
				// �������50m,����ʱ����tmͨ��
				nextLinkId = currentLinkId;
			}
		}
		// �յ���ͬ
		if(nextLinkId==null || destNodeId==null || destNodeId.intValue()==newDestNodeId.intValue()) {
			coreListener.onPathChanged(false, null, null, null);
			LogUtil.verbose("change destination failed.");
			String hint = CommonUtil.catString("destNodeId: ", destNodeId, "newDestNode: ", newDestNodeId);
			Log.e(TAG,"currentLinkId: "+currentLinkId);
			Log.e(TAG,"nextlinkid: "+nextLinkId);
			Log.e(TAG, "destNodeId: " + destNodeId);
			Log.e(TAG, "newDestNodeId: " + newDestNodeId);
			LogUtil.verbose(hint);
			return;
		}
		tempLinkId = nextLinkId;
		Integer tempDestNodeId = dataConfig.getEndNodeIdOfLink(nextLinkId);
		Point startPoint = dataConfig.getEndPointOfLink(nextLinkId);
		Point endPoint = dataConfig.getLatLngOfNode(newDestNodeId);
		ppTask.startTask(vehicle.getId(), startPoint, endPoint, tempDestNodeId);
	}
	
	/**
	 * �յ����������󣬿�ʼ����·���滮
	 */
	private void handleRequestCharge() {
		double currentLat = vehicle.getLatitude();
		double currentLng = vehicle.getLongitude();
		Integer nearestNodeId = dataConfig.getNearstStation(currentLat, currentLng);
		handleChangeDest(nearestNodeId);
	}
	
	/**
	 * �յ����ĳ���������
	 * @param subInfo
	 */
	private void onReceiveSubInfoData(SubInfo subInfo) {
		// just for debug
		// Log.d("DEBUG", "Subscribe Information below");
		// Log.d("DEBUG", "latitude: " + subInfo.latitude);
		// Log.d("DEBUG", "longitude: " + subInfo.longitude);
		// Log.d("DEBUG", "charge: " + subInfo.currentCharge);
		// Log.d("DEBUG", "speed: " + subInfo.speed);
		// Log.d("DEBUG", "link id: " + subInfo.linkId);
		// ��ǰlinkid
		setCurrentLink(subInfo.linkId);
		// ��ǰλ��
		if (setLocation(subInfo.latitude, subInfo.longitude)) {
			coreListener.onLocationChanged(new Point(subInfo.latitude, subInfo.longitude));
		}
		// ��ǰ����
		setCharge(subInfo.currentCharge);
		// ��ǰ�ٶ�
		setSpeed(subInfo.speed);
		
		//��ǰ״̬
		setStatus(subInfo.status);
		if(coreListener != null) {
			coreListener.onOtherInfoChanged(subInfo.currentCharge, subInfo.speed, subInfo.linkId,subInfo.status);
		}
	}
	
	/**
	 * �յ��µ�·���滮����
	 * @param pathInfo
	 */
	private void onReceivePathInfoData(PathInfo pathInfo)  {
		List<Integer> links = pathInfo.links;
		List<Point> nodes = pathInfo.pathNodes;
		// ������ȷ�ϣ����ܻ�ûת����Ҫ���ϵ�ǰ�㵽�¹滮����ʼ���link
		// ���ڵ�ͼչʾ
		if(tempLinkId!=null && tempLinkId.intValue()!=0) {
			Integer currentLinkId = vehicle.getLinkID();
			if(currentLinkId.intValue() == tempLinkId.intValue()) {
				links.add(0, currentLinkId);
				nodes.add(0, new Point(vehicle.getLatitude(), vehicle.getLongitude()));
			}
			else
			{
				links.add(0, tempLinkId);
				links.add(0, currentLinkId);
				nodes.add(0, dataConfig.getEndPointOfLink(currentLinkId));
				nodes.add(0, new Point(vehicle.getLatitude(), vehicle.getLongitude()));
			}
		}
		vehicle.setPath(links);
		vehicle.setStartPos(dataConfig.getStartNodeIdOfLink(links.get(0)));
		vehicle.setEndPos(dataConfig.getEndNodeIdOfLink(links.get(links.size()-1)));
		// ��ǰ�˵Ļص�
		if(coreListener != null) {
			coreListener.onPathChanged(true, nodes, nodes.get(0), nodes.get(nodes.size()-1));
		}
	}
	
	/**
	 * �յ�receiveData��Ϣ�Ĵ�����
	 * @param dataType ��������
	 * @param data ����ʵ��
	 */
	private void handleReceiveData(int dataType, Object data) {
		if(coreListener == null) {
			LogUtil.error("CoreThread: coreListener is null.");
			return;
		}
		if(data == null) {
			mHandler.obtainMessage(MSG_ON_ERROR, ERROR_EMPTY_DATA, -1).sendToTarget();
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
			onReceivePathInfoData((PathInfo) data);
			break;
		}
	}
	
	private void handleOnError(int errorType, Object detail) {
		switch(errorType) {
		case ERROR_ON_INIT:
			System.out.println("caonima");
			if(stateListener!=null) stateListener.onInitStatus(INIT_STATUS_FAILED);
			sendMessage(MSG_ON_QUIT);
			break;
		case ERROR_INIT_VEHICLE:
			if(stateListener!=null) stateListener.onInitStatus(INIT_STATUS_FAILED);
			sendMessage(MSG_ON_QUIT);
			break;
		default:
			LogUtil.error(TAG, "error type: " + errorType);
		}
	}
	
	/**
	 * �����ǹ��ڳ�����Ϣ�ĺ���
	 */
	/**
	 * vehicle �Ƿ����
	 * @return
	 */
	private boolean isExistVehicle() {
		if(vehicle == null) {
			LogUtil.error("entity vehicle is null.");
			// coreListener.onError(ERROR_NULL_POINTER);
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
	
	/**
	 * ���õ�ǰ����,��������и����򷵻�true�����򷵻�false
	 * @param charge
	 * @return
	 */
	private boolean setCharge(double charge){
		if(!isExistVehicle())
			return false;
		if(vehicle.getCharge() != charge) {
			vehicle.setCharge(charge);
			return true;
		}
		return false;
	}
	
	/**
	 * ���õ�ǰlink,���link id�и����򷵻�true
	 * @param linkid
	 * @return
	 */
	private boolean setCurrentLink(int linkid) {
		if(!isExistVehicle())
			return false;
		if(linkid == 0)
			return false;
		if(vehicle.getLinkID().intValue() != linkid) {
			vehicle.setLinkID(Integer.valueOf(linkid));
			return true;
		}
		return false;
	}
	
	/**
	 * ���õ�ǰ�ٶ�,���ٶ��и����򷵻�true
	 * @param speed
	 * @return
	 */
	private boolean setSpeed(double speed) {
		if(!isExistVehicle())
			return false;
		if(vehicle.getSpeed().doubleValue() != speed) {
			vehicle.setSpeed(Double.valueOf(speed));
			return true;
		}
		return false;
	}
	
	//���õ�ǰ����״̬
	private boolean setStatus(Integer status){	
		if(!isExistVehicle())
			return false;
		if(vehicle.getStatus() != status) {
			vehicle.setStatus(status);
			return true;
		}
		return false;	
	}
	
	private boolean isNowCharging() {
		/*
		if(vehicle.isNowCharging()) {
			coreListener.onError(ERROR_ON_CHARGING);
			return true;
		}
		return false;
		*/
		return vehicle.isNowCharging();
	}
}

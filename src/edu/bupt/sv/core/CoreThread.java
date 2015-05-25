package edu.bupt.sv.core;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import edu.bupt.sv.entity.PathInfo;
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
	
	// 任务对象
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
		if (null == dataConfig) {
			dataConfig = DataConfig.getInstance(mContext);
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
	 * 初始化车辆并向TM订阅车辆信息
	 * @param vehicleId
	 */
	private void handleInitVehicle(Integer vehicleId) {
		// 从vehicleList中选一个
		Vehicle v = dataConfig.getVehicleFromConfig(vehicleId);
		Assert.assertNotNull(v);
		this.vehicle = v;
		// 给出车的初始化信息
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
		// 订阅车辆信息
		boolean ret = tmAccessor.requestInitVehicle(vehicleId);
		if(!ret && coreListener != null) {
			coreListener.onError(ERROR_INIT_VEHICLE);
		}
	}
	
	/**
	 * 收到转向请求，开始路径规划
	 * @param direction
	 */
	private void handlePathPlan(Integer direction) {
		// 获得当前的linkid
		Integer currentLinkId = vehicle.getLinkID();
		// 获得规划的路径的下一个link
		Integer nextLinkId = vehicle.getNextLinkOfPath(currentLinkId);
		// 获取期望转向的下一个link
		Integer turnLinkId = dataConfig.getTurnLink(currentLinkId, direction.intValue());
		// 不能转向或者与路径规划相同
		if(nextLinkId==null || turnLinkId==null || nextLinkId.equals(turnLinkId)) {
			coreListener.onPathChanged(false, null, null, null);
			LogUtil.verbose("turn new path failed.");
			String hint = CommonUtil.catString("currentLinkId: ", currentLinkId, "nextLink: ", nextLinkId, "turnLinkId: ",turnLinkId);
			LogUtil.verbose(hint);
			return;
		}
		List<Integer> tempPath = new ArrayList<Integer>();
		tempPath.add(currentLinkId);
		tempPath.add(turnLinkId);
		Point startPoint = dataConfig.getEndPointOfLink(turnLinkId);
		Integer endNodeId = vehicle.getEndPos();
		Point endPoint = dataConfig.getLatLngOfNode(endNodeId);
		ppTask.startTask(startPoint, endPoint, tempPath);
	}
	
	private void handleChangeDest(Integer newDestNodeId) {
		// 获得当前的linkid
		Integer currentLinkId = vehicle.getLinkID();
		// 获得规划的路径的下一个link
		Integer nextLinkId = vehicle.getNextLinkOfPath(currentLinkId);
		// 获得规划的路径的终点
		Integer destNodeId = vehicle.getEndPos();
		// 终点相同
		if(nextLinkId==null || destNodeId==null || destNodeId.equals(newDestNodeId)) {
			coreListener.onPathChanged(false, null, null, null);
			LogUtil.verbose("change destination failed.");
			String hint = CommonUtil.catString("destNodeId: ", destNodeId, "newDestNode: ", newDestNodeId);
			LogUtil.verbose(hint);
			return;
		}
		List<Integer> tempPath = new ArrayList<Integer>();
		tempPath.add(currentLinkId);
		tempPath.add(nextLinkId);
		Point startPoint = dataConfig.getEndPointOfLink(nextLinkId);
		Point endPoint = dataConfig.getLatLngOfNode(newDestNodeId);
		ppTask.startTask(startPoint, endPoint, tempPath);
	}
	
	private void onReceiveSubInfoData(SubInfo subInfo) {
		// just for debug
		// LogUtil.warn("Current link: " + subInfo.linkId);
		 LogUtil.warn("@@SubInfo: " + subInfo.latitude + " " + subInfo.longitude);
		// 当前linkid
		setCurrentLink(subInfo.linkId);
		// 当前位置
		if (setLocation(subInfo.latitude, subInfo.longitude)) {
			LogUtil.warn("##SubInfo: " + subInfo.latitude + " " + subInfo.longitude);
			coreListener.onLocationChanged(new Point(subInfo.latitude, subInfo.longitude));
		}
		// 当前电量
		if (setCharge(subInfo.currentCharge)) {
			coreListener.onChargedChanged(subInfo.currentCharge);
		}
	}
	
	private void onReceivePathInfoData(PathInfo pathInfo) {
		List<Integer> links = pathInfo.links;
		List<Point> nodes = pathInfo.pathNodes;
		// TODO 合法性确认，可能还没转呢
		vehicle.setPath(links);
		vehicle.setStartPos(dataConfig.getStartNodeIdOfLink(links.get(0)));
		vehicle.setEndPos(dataConfig.getEndNodeIdOfLink(links.get(links.size()-1)));
		// 给前端的回调
		if(coreListener != null) {
			coreListener.onPathChanged(true, nodes, nodes.get(0), nodes.get(nodes.size()-1));
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
			onReceivePathInfoData((PathInfo) data);
			break;
		}
	}
	
	/**
	 * 以下是关于车辆信息的函数
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
	 * 设置位置，如果位置有更新则返回true，否则返回false
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

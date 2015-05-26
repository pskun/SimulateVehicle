package edu.bupt.sv.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.bupt.sv.core.MsgConstants;
import edu.bupt.sv.entity.SubInfo;
import edu.bupt.sv.tm.NTYMessage;
import edu.bupt.sv.tm.TMListener;
import edu.bupt.sv.tm.TMMessageHandler;
import edu.bupt.sv.utils.CommonUtil;
import edu.bupt.sv.utils.LogUtil;
import android.os.Handler;

public class TMAccessor implements NetworkConstants, MsgConstants {

	private TMMessageHandler tmMsgHandler = null;
	private Handler coreHandler;
	private Handler jobHandler;
	
	private int nextVehicleListSendId;
	private int nextChangeDestSendId;
	private int nextChangePathSendId;
	private Set<Integer> nextInitVehicleSendSet;
	// 订阅车辆的ID集合，该集合容量为5
	private Set<Integer> subVehicleSet; 
	
	private TMListener tmListener = new TMListener() {
		@Override
		public void onReceiveNTY(final int ID, NTYMessage NTY) {
			LogUtil.verbose("tm on receive data, ID: " + ID);
			if (ID == nextVehicleListSendId) {
				handleVehicleListNTY(NTY);
			} else if (nextInitVehicleSendSet.contains(ID)) {
				handleInitVehicleNTY(NTY);
			} else if (ID == nextChangeDestSendId) {
				if(checkResponseCode(NTY.NTY.Code)) {
					jobHandler.obtainMessage(MSG_ON_RECEIVE, DATA_TM_DEST_ACK, -1);
				} else {
					// TODO
					// 处理错误
				}
			} else if (ID == nextChangePathSendId) {
				if(checkResponseCode(NTY.NTY.Code)) {
					jobHandler.obtainMessage(MSG_ON_RECEIVE, DATA_TM_PATH_ACK, -1);
				} else {
					// TODO
					// 处理错误
				}
			}
		}
	};

	public TMAccessor(Handler coreHandler) {
		super();
		this.coreHandler = coreHandler;
	}

	public void init() {
		LogUtil.verbose("tmAccessor: begin initialize tmAccessor.");
		if (null != tmMsgHandler) {
			LogUtil.warn("tmMsgHandler already exists.");
			return;
		}
		tmMsgHandler = new TMMessageHandler(tmListener);
		nextVehicleListSendId = -1;
		nextChangeDestSendId = -1;
		nextChangePathSendId = -1;
		
		if(null != subVehicleSet) subVehicleSet.clear();
		else nextInitVehicleSendSet = new HashSet<Integer>(5);
		
		if(null != subVehicleSet) subVehicleSet.clear();
		else subVehicleSet = new HashSet<Integer>(5);
		
		LogUtil.verbose("tmAccessor: initialize tmAccessor done.");
	}

	public void destroy() {
		if (tmMsgHandler != null) {
			tmMsgHandler.close();
			tmMsgHandler = null;
		}
		nextVehicleListSendId = -1;
		nextChangeDestSendId = -1;
		nextChangePathSendId = -1;
		
		if(null != nextInitVehicleSendSet) nextInitVehicleSendSet.clear();
		
		if(null != subVehicleSet) subVehicleSet.clear();
	}
	
	public void setJobHandler(Handler jobHandler) {
		this.jobHandler = jobHandler;
	}
	
	public boolean requestAllVehicle() {
		int size = 200;
		nextVehicleListSendId = tmMsgHandler.sendSubAllVehicle(size);
		LogUtil.verbose("TMAccessor: message of requestAllVehicle was sent, id: " + nextVehicleListSendId);
		return true;
	}

	public boolean requestInitVehicle(Integer vehicleId) {
		if (vehicleId == null) {
			LogUtil.error("requestInitVehicle: vehicle id is null.");
			return false;
		}
		if (subVehicleSet.contains(vehicleId))
			return true;
		List<Integer> vi = new ArrayList<Integer>();
		vi.add(vehicleId);
		int nextInitVehicleSendId = tmMsgHandler.sendSubVehicleConstantly(vi);
		nextInitVehicleSendSet.add(Integer.valueOf(nextInitVehicleSendId));
		subVehicleSet.add(vehicleId);
		LogUtil.verbose("TMAccessor: message of requestAllVehicle was sent, id: " + nextInitVehicleSendId);
		return true;
	}

	public boolean requestChangeDest(Integer vehicleId, Integer destNodeId) {
		if (vehicleId == null) {
			LogUtil.error("requestChangeDest: vehicle id is null.");
			return false;
		}
		List<Integer> vi = new ArrayList<Integer>();
		vi.add(vehicleId);
		List<Integer> did = new ArrayList<Integer>();
		did.add(destNodeId);
		nextChangeDestSendId = tmMsgHandler.sendChangeDst(vi, did);
		LogUtil.verbose("TMAccessor: message of requestChangeDest was sent, id: " + nextChangeDestSendId);
		return true;
	}
	
	public boolean requestChangePath(Integer vehicleId, List<Integer> links) {
		if (vehicleId == null) {
			LogUtil.error("requestChangeDest: vehicle id is null.");
			return false;
		}
		List<Integer> vi = new ArrayList<Integer>();
		vi.add(vehicleId);
		List<List<Integer>> ls = new ArrayList<List<Integer>>();
		ls.add(links);
		List<List<Integer>> cs = new ArrayList<List<Integer>>();
		nextChangePathSendId = tmMsgHandler.sendChangePath(vi, ls, cs);
		LogUtil.verbose("TMAccessor: message of requestChangePath was sent, id: " + nextChangePathSendId);
		return true;
	}
	
	private boolean checkResponseCode(int code) {
		// LogUtil.verbose("respond code: " + code);
		// 判断响应码
		if (RES_SUCCESS_CODE != code && RES_PART_SUCCESS_CODE != code) {
			if (coreHandler != null) {
				String hint = null;
				if (RES_ERROR_CODE == code)
					hint = "请求消息错误";
				else if (RES_NO_DATA_CODE == code)
					hint = "没有需要的数据";
				coreHandler.obtainMessage(MSG_ON_ERROR, hint).sendToTarget();
			}
			return false;
		}
		return true;
	}

	private void handleVehicleListNTY(NTYMessage NTY) {
		LogUtil.verbose("handle vehicle list NTY");
		if(!checkResponseCode(NTY.NTY.Code))
			return;
		// 响应成功的处理
		List<Integer> vehicleIdList = NTY.NTY.VI;			
		if (vehicleIdList != null && vehicleIdList.size() > 0) {
			if (coreHandler != null) {
				coreHandler.obtainMessage(MSG_ON_RECEIVE, DATA_VEHICLE_LIST,
						-1, vehicleIdList).sendToTarget();
			}
		}
	}

	private void handleInitVehicleNTY(NTYMessage NTY) {
		if(!checkResponseCode(NTY.NTY.Code))
			return;
		// 响应成功的处理
		SubInfo info = new SubInfo();
		// 经纬度
		// 需要转换经纬度
		if(NTY.NTY.Lad != null) {
			Integer lat = NTY.NTY.Lad.get(0);
			info.latitude = CommonUtil.convertToValidLatLng(lat);
		}
		if(NTY.NTY.Longd != null) {
			Integer lng = NTY.NTY.Longd.get(0);
			info.longitude = CommonUtil.convertToValidLatLng(lng);
		}
		// 速度
		if(NTY.NTY.Speed != null)
			info.speed = NTY.NTY.Speed.get(0);
		// 当前电量
		if(NTY.NTY.Elec != null)
			info.currentCharge = NTY.NTY.Elec.get(0);
		// 当前linkid
		if(NTY.NTY.Link != null) {
			info.linkId = NTY.NTY.Link.get(0);
		}
		if(coreHandler != null) {
			coreHandler.obtainMessage(MSG_ON_RECEIVE, DATA_VEHICLE_INFO, -1, info).sendToTarget();
		}
	}
}

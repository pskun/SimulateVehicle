package edu.bupt.sv.service;

import java.util.ArrayList;
import java.util.List;

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
	private int nextInitVehicleSendId;
	private int nextChangeDestSendId;

	private boolean isTempChangeDest = false;
	
	private TMListener tmListener = new TMListener() {
		@Override
		public void onReceiveNTY(final int ID, NTYMessage NTY) {
			LogUtil.verbose("tm on receive data, ID: " + ID);
			if (ID == nextVehicleListSendId) {
				handleVehicleListNTY(NTY);
			} else if (ID == nextInitVehicleSendId) {
				handleInitVehicleNTY(NTY);
			} else if (ID == nextChangeDestSendId) {
				if(checkResponseCode(NTY.NTY.Code)) {
					jobHandler.obtainMessage(MSG_CHANGE_DEST, new Boolean(isTempChangeDest)).sendToTarget();
				}
				else {
					jobHandler.obtainMessage(MSG_ON_ERROR).sendToTarget();
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
		nextInitVehicleSendId = -1;
		nextChangeDestSendId = -1;
		LogUtil.verbose("tmAccessor: initialize tmAccessor done.");
	}

	public void destroy() {
		if (tmMsgHandler != null) {
			tmMsgHandler.close();
			tmMsgHandler = null;
		}
		nextVehicleListSendId = -1;
		nextInitVehicleSendId = -1;
	}
	
	public void setJobHandler(Handler jobHandler) {
		this.jobHandler = jobHandler;
	}
	
	public boolean requestAllVehicle() {
		int size = 200;
		if (tmMsgHandler == null) {
			LogUtil.error("requestAllVehicle: tmMsgHanlder is null.");
			return false;
		}
		nextVehicleListSendId = tmMsgHandler.sendSubAllVehicle(size);
		LogUtil.verbose("TMAccessor: message of requestAllVehicle was sent, id: " + nextVehicleListSendId);
		return true;
	}

	public boolean requestInitVehicle(Integer vehicleId) {
		if (tmMsgHandler == null) {
			LogUtil.error("requestInitVehicle: tmMsgHanlder is null.");
			return false;
		}
		if (vehicleId == null) {
			LogUtil.error("requestInitVehicle: vehicle id is null.");
			return false;
		}
		List<Integer> vi = new ArrayList<Integer>();
		vi.add(vehicleId);
		nextInitVehicleSendId = tmMsgHandler.sendSubVehicleConstantly(vi);
		LogUtil.verbose("TMAccessor: message of requestAllVehicle was sent, id: " + nextInitVehicleSendId);
		return true;
	}

	public boolean requestChangeDest(Integer vehicleId, List<Integer> links, boolean isTemporary) {
		this.isTempChangeDest = isTemporary;
		if (tmMsgHandler == null) {
			LogUtil.error("requestChangeDest: tmMsgHanlder is null.");
			return false;
		}
		if (vehicleId == null) {
			LogUtil.error("requestChangeDest: vehicle id is null.");
			return false;
		}
		List<Integer> vi = new ArrayList<Integer>();
		vi.add(vehicleId);
		nextChangeDestSendId = tmMsgHandler.sendChangeDst(vi, links);
		LogUtil.verbose("TMAccessor: message of requestChangeDest was sent, id: " + nextChangeDestSendId);
		return true;
	}
	
	private boolean checkResponseCode(int code) {
		LogUtil.verbose("respond code: " + code);
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

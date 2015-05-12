package edu.bupt.sv.service;

import java.util.List;

import edu.bupt.sv.core.MsgConstants;
import edu.bupt.sv.tm.NTYMessage;
import edu.bupt.sv.tm.TMListener;
import edu.bupt.sv.tm.TMMessageHandler;
import edu.bupt.sv.utils.LogUtil;
import android.os.Handler;

public class TMAccessor implements NetworkConstants, MsgConstants {

	public TMMessageHandler tmMsgHandler = null;
	private Handler coreHandler;
	private int nextVehicleListSendId;
	
	public TMListener tmListener = new TMListener() {
		@Override
		public void onReceiveNTY(final int ID, NTYMessage NTY) {
			if (ID == nextVehicleListSendId) {
				handleRequestVehicleList(NTY);
			}
		}
	};
	
	public TMAccessor(Handler coreHandler) {
		super();
		this.coreHandler = coreHandler;
	}
	
	public void init()
	{
		if(null != tmMsgHandler)
		{
			LogUtil.warn("tmMsgHandler already exists.");
			return;
		}
		tmMsgHandler = new TMMessageHandler(tmListener);
		nextVehicleListSendId = -1;
	}
	
	public boolean requestAllVehicle()
	{
		int size = 200;
		if (tmMsgHandler == null) {
			LogUtil.error("requestAllVehicle: tmMsgHanlder is null.");
			return false;
		}
		nextVehicleListSendId = tmMsgHandler.sendSubAllVehicle(size);
		return false;
	}
	
	private void handleRequestVehicleList(NTYMessage NTY) {
		// 判断响应码
		if (RES_SUCCESS_CODE != NTY.NTY.Code) {
			if (coreHandler != null) {
				String hint = null;
				if (RES_ERROR_CODE == NTY.NTY.Code)
					hint = "请求消息错误";
				else if(RES_NO_DATA_CODE == NTY.NTY.Code)
					hint = "没有需要的数据";
				coreHandler.obtainMessage(MSG_ON_ERROR, hint).sendToTarget();
			}
			return;
		}
		// 响应成功的处理
		List<Integer> vehicleIdList = NTY.NTY.VI;
		if(vehicleIdList != null && vehicleIdList.size() > 0) {
			if (coreHandler != null) {
				coreHandler.obtainMessage(MSG_ON_RECEIVE, DATA_VEHICLE_LIST, -1, vehicleIdList);
			}
		}
	}
}

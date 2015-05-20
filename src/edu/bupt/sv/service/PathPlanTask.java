package edu.bupt.sv.service;

import java.util.List;

import edu.bupt.sv.core.MsgConstants;
import edu.bupt.sv.entity.PathInfo;
import edu.bupt.sv.entity.Point;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class PathPlanTask implements Runnable, MsgConstants {

	private static final int LOCAL_MSG_START_PLAN = 1;
	
	private Handler coreHandler;
	private Handler mLocalHandler;
	
	private TMAccessor tmAccessor;
	private PlatformAccessor pfAccessor;
	
	private Integer vehicleId;
	private List<Integer> tempLinks;
	private Point startPoint;
	private Point destPoint;
	
	private boolean tempDestChangeACK = false;
	private boolean finalDestChangeACK = false;
	
	private PathInfo pathInfo = null;
	
	public void startTask() {
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		Looper.prepare();
		mLocalHandler = new Handler(Looper.myLooper()) {
			@Override
            public void handleMessage(Message msg) {
				super.handleMessage(msg);
                handleLocalMessage(msg);
            }
		};
		mLocalHandler.obtainMessage(LOCAL_MSG_START_PLAN);
		Looper.loop();
	}

	public void handleLocalMessage(Message msg) {
		switch(msg.what) {
		case LOCAL_MSG_START_PLAN:
			handleStartPlan();
			break;
		case MSG_CHANGE_DEST:
			handleReceiveNewDest((Boolean) msg.obj);
			break;
		case MSG_ON_RECEIVE:
			handleReceiveNewPath((PathInfo) msg.obj);
			break;
		case MSG_ON_ERROR:
			handleOnError();
			break;
		}
	}
	
	private void handleStartPlan() {
		// 告知TM临时终点
		tmAccessor.setJobHandler(mLocalHandler);
		tmAccessor.requestChangeDest(vehicleId, tempLinks, true);
		// 进行路径规划
		pfAccessor.planPath(startPoint.latitude, startPoint.longitude, destPoint.latitude, destPoint.longitude);
	}
	
	private void handleReceiveNewPath(PathInfo newPathInfo) {
		this.pathInfo = newPathInfo;
		// 告知TM最终的终点和路径
		tmAccessor.requestChangeDest(vehicleId, tempLinks, false);
	}
	
	private void handleReceiveNewDest(Boolean isTemp) {
		if(isTemp.booleanValue())
			tempDestChangeACK = true;
		else
			finalDestChangeACK = true;
		if (tempDestChangeACK && finalDestChangeACK) {
			// 改变终点完成
			// 此时必然有新的路径，没有就出错了
			if (pathInfo == null) {
				mLocalHandler.obtainMessage(MSG_ON_ERROR).sendToTarget();
				return;
			}
			coreHandler.obtainMessage(MSG_ON_RECEIVE, DATA_PATH_PLAN, -1, pathInfo);
		}
	}
	
	private void handleOnError() {
		// TODO
	}
}

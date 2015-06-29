package edu.bupt.sv.service;

import edu.bupt.sv.core.MsgConstants;
import edu.bupt.sv.entity.PathInfo;
import edu.bupt.sv.entity.Point;
import edu.bupt.sv.utils.LogUtil;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class PathPlanTask implements Runnable, MsgConstants {

	private static final String TAG = "PathPlanTask";
	
	private static final int LOCAL_MSG_START_PLAN = 1;
	private static final int LOCAL_MSG_QUIT = 2;
	
	private Handler coreHandler;
	private Handler mLocalHandler;
	
	private TMAccessor tmAccessor;
	private PlatformAccessor pfAccessor;
	
	
	private Integer vehicleId;
	private Integer tempDestNodeId;
	private Point startPoint;
	private Point destPoint;
	
	private boolean tempDestChangeACK = false;
	private boolean finalDestChangeACK = false;
	
	private PathInfo pathInfo = null;
	
	private Thread jobThread;
	
	public PathPlanTask(Context ctx, Handler coreHandler, TMAccessor tmAccessor) {
		super();
		this.coreHandler = coreHandler;
		this.tmAccessor = tmAccessor;
		this.pfAccessor = new PlatformAccessor(ctx);
	}


	public void startTask(Integer vehicleId,Point startPoint, Point destPoint, Integer tempDestNodeId) {
		if(jobThread!=null && jobThread.isAlive()) {
			handleOnQuit();
		}

		// ��ʼ��
		this.vehicleId = vehicleId;
		tempDestChangeACK = false;
		finalDestChangeACK = false;
		this.tempDestNodeId = tempDestNodeId;
		// ���������յ�
		this.startPoint = startPoint;
		this.destPoint = destPoint;
		// ��ʼ�滮
		jobThread = new Thread(this);
		jobThread.start();
	}
	
	public void destroy() {
		if(null != pfAccessor) {
			pfAccessor.destroy();
			pfAccessor = null;
		}
		if(mLocalHandler!=null) {
			mLocalHandler.removeCallbacksAndMessages(null);
			mLocalHandler.obtainMessage(LOCAL_MSG_QUIT).sendToTarget();
		}
	}
	
	@Override
	public void run() {
		Log.e(TAG, "thread started");
		Looper.prepare();
		mLocalHandler = new Handler(Looper.myLooper()) {
			@Override
            public void handleMessage(Message msg) {
				super.handleMessage(msg);
                handleLocalMessage(msg);
            }
		};
		mLocalHandler.obtainMessage(LOCAL_MSG_START_PLAN).sendToTarget();
		Looper.loop();
	}

	private void handleLocalMessage(Message msg) {
		switch(msg.what) {
		case LOCAL_MSG_START_PLAN:
			handleStartPlan();
			break;
		case MSG_ON_RECEIVE:
			handleOnReceive(((Integer) msg.arg1).intValue(), msg.obj);
			break;
		case MSG_ON_ERROR:
			handleOnError();
			break;
		case LOCAL_MSG_QUIT:
			handleOnQuit();
			break;
		}
	}
	
	private void handleStartPlan() {
		//
		tmAccessor.setJobHandler(mLocalHandler);
		pfAccessor.setJobHandler(mLocalHandler);
		// ��֪TM��ʱ�յ�
		Log.e(TAG, "pathPlanTask handle start plan");
		Log.e(TAG, "vehicleId: " + vehicleId);
		Log.e(TAG, "tempDestNodeId: " + tempDestNodeId);
		tmAccessor.requestChangeDest(vehicleId, tempDestNodeId);
		// ����·���滮
		pfAccessor.planPath(startPoint.latitude, startPoint.longitude, destPoint.latitude, destPoint.longitude);
	}
	
	private void handleOnReceive(int dataType, Object obj) {
		Log.e(TAG, "on receive data:" + dataType);
		switch(dataType) {
		case DATA_TM_DEST_ACK:
			tempDestChangeACK = true;
			break;
		case DATA_TM_PATH_ACK:
			finalDestChangeACK = true;
			break;
		case DATA_PATH_PLAN:
			this.pathInfo = (PathInfo) obj;
			// ��֪TM���յ��յ��·��
			tmAccessor.requestChangePath(vehicleId, this.pathInfo.links);
			break;
		}
		if (tempDestChangeACK && finalDestChangeACK) {
			// �ı��յ����
			// ��ʱ��Ȼ���µ�·����û�оͳ�����
			if (pathInfo == null) {
				mLocalHandler.obtainMessage(MSG_ON_ERROR).sendToTarget();
				return;
			}
			Log.e(TAG, "pathplantask receive new path");
			// �յ��µ�·������֪coreThread
			coreHandler.obtainMessage(MSG_ON_RECEIVE, DATA_PATH_PLAN, -1, pathInfo).sendToTarget();
			// �˳�
			mLocalHandler.obtainMessage(LOCAL_MSG_QUIT).sendToTarget();
		}
	}
	
	private void handleOnError() {
		coreHandler.obtainMessage(MSG_ON_ERROR).sendToTarget();
		// �˳�
		this.destroy();
	}
	
	private void handleOnQuit() {
		if(mLocalHandler!=null) {
			mLocalHandler.removeCallbacksAndMessages(null);
		}
		Looper.myLooper().quit();
		jobThread.interrupt();
		mLocalHandler = null;
		LogUtil.verbose("ppTask is now destroyed.");
	}
}

package edu.bupt.sv.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.bupt.sv.core.MsgConstants;
import edu.bupt.sv.entity.PathInfo;
import edu.bupt.sv.entity.Point;
import edu.bupt.sv.utils.LogUtil;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class PathPlanTask implements Runnable, MsgConstants {

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
	
	private ExecutorService threadPool = Executors.newSingleThreadExecutor();
	
	public PathPlanTask(Handler coreHandler, TMAccessor tmAccessor) {
		super();
		this.coreHandler = coreHandler;
		this.tmAccessor = tmAccessor;
	}

	public void startTask(Point startPoint, Point destPoint, Integer tempDestNodeId) {
		// ��ʼ��
		tempDestChangeACK = false;
		finalDestChangeACK = false;
		this.tempDestNodeId = tempDestNodeId;
		// ���������յ�
		this.startPoint = startPoint;
		this.destPoint = destPoint;
		// ��ʼ�滮
		threadPool.execute(this);
	}
	
	public void destroy() {
		if(null != tmAccessor) {
			tmAccessor.destroy();
			tmAccessor = null;
		}
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
		tmAccessor.setJobHandler(mLocalHandler);
		// ��֪TM��ʱ�յ�
		tmAccessor.requestChangeDest(vehicleId, tempDestNodeId);
		// ����·���滮
		pfAccessor.planPath(startPoint.latitude, startPoint.longitude, destPoint.latitude, destPoint.longitude);
	}
	
	private void handleOnReceive(int dataType, Object obj) {
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
			// �յ��µ�·������֪coreThread
			coreHandler.obtainMessage(MSG_ON_RECEIVE, DATA_PATH_PLAN, -1, pathInfo);
			// �˳�
			mLocalHandler.obtainMessage(LOCAL_MSG_QUIT).sendToTarget();
		}
	}
	
	private void handleOnError() {
		// TODO
	}
	
	private void handleOnQuit() {
		Looper.myLooper().quit();
		LogUtil.verbose("ppTask is now destroyed.");
	}
}

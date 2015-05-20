package edu.bupt.sv.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	
	private ExecutorService threadPool = Executors.newSingleThreadExecutor();
	
	public PathPlanTask(Handler coreHandler, TMAccessor tmAccessor) {
		super();
		this.coreHandler = coreHandler;
		this.tmAccessor = tmAccessor;
	}

	public void startTask(Point startPoint, Point destPoint) {
		// ��ʼ��
		tempDestChangeACK = false;
		finalDestChangeACK = false;
		tempLinks = null;
		// ���������յ�
		this.startPoint = startPoint;
		this.destPoint = destPoint;
		// ��ʼ�滮
		threadPool.execute(this);
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
		tmAccessor.setJobHandler(mLocalHandler);
		// ��֪TM��ʱ�յ�
		tmAccessor.requestChangeDest(vehicleId, tempLinks, true);
		// ����·���滮
		pfAccessor.planPath(startPoint.latitude, startPoint.longitude, destPoint.latitude, destPoint.longitude);
	}
	
	private void handleReceiveNewPath(PathInfo newPathInfo) {
		this.pathInfo = newPathInfo;
		// ��֪TM���յ��յ��·��
		tmAccessor.requestChangeDest(vehicleId, tempLinks, false);
	}
	
	private void handleReceiveNewDest(Boolean isTemp) {
		if(isTemp.booleanValue())
			tempDestChangeACK = true;
		else
			finalDestChangeACK = true;
		if (tempDestChangeACK && finalDestChangeACK) {
			// �ı��յ����
			// ��ʱ��Ȼ���µ�·����û�оͳ�����
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

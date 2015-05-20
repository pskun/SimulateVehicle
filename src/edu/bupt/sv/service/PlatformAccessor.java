package edu.bupt.sv.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import edu.bupt.sv.core.MsgConstants;
import edu.bupt.sv.entity.PathInfo;
import edu.bupt.sv.entity.Point;
import edu.bupt.sv.utils.CommonUtil;
import edu.bupt.sv.utils.LogUtil;
import android.content.Context;
import android.os.Handler;

public class PlatformAccessor implements NetworkConstants, MsgConstants {

	private static final String FIELD_STATUS = "status";
	private static final String FIELD_PATHNODE = "pathNode";
	private static final String FIELD_LINKS = "links";
	private static final String FIELD_LATITUDE = "latitude";
	private static final String FIELD_LONGITUDE = "longitude";
	
	private Context context;
	private Handler targetHandler;

	private AsyncHttpClient client;
	
	private boolean checkResponseStatus(JSONObject response) {
		try {
			String status = response.getString(FIELD_STATUS);
			if(CommonUtil.isStringNull(status) || !"OK".equals(status))
				return false;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private JsonHttpResponseHandler planResHandler = new JsonHttpResponseHandler() {
		@Override
		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			LogUtil.warn("http request failed: statusCode is" + statusCode);
			if(targetHandler != null) {
				targetHandler.obtainMessage(MSG_ON_ERROR).sendToTarget();
			}
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			if(!checkResponseStatus(response)) {
				LogUtil.warn("response json data is wrong: status is not OK");
				if(targetHandler != null) {
					targetHandler.obtainMessage(MSG_ON_ERROR).sendToTarget();
				}
				return;
			}
			try {
				JSONArray ja = response.getJSONArray(FIELD_PATHNODE);
				JSONArray ls = response.getJSONArray(FIELD_LINKS);
				
				List<Point> pathNodes = new ArrayList<Point>();
				for (int i = 0; i < ja.length(); i++) {
					double latitude = ja.getJSONObject(i).getDouble(FIELD_LATITUDE);
					double longitude = ja.getJSONObject(i).getDouble(FIELD_LONGITUDE);
					Point p = new Point(latitude, longitude);
					pathNodes.add(p);
				}
				List<Integer> links = new ArrayList<Integer>();
				for (int i = 0; i < ls.length(); i++) {
					Integer link = new Integer(ls.getInt(i));
					links.add(link);
				}
				LogUtil.verbose("PlatformAccessor: success to parse path json data.");
				PathInfo pathInfo = new PathInfo(pathNodes, links);
				if(targetHandler != null) {
					targetHandler.obtainMessage(MSG_ON_RECEIVE, DATA_PATH_PLAN, -1, pathInfo).sendToTarget();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				LogUtil.warn("response json data is wrong: throw json exception.");
				if(targetHandler != null) {
					targetHandler.obtainMessage(MSG_ON_ERROR).sendToTarget();
				}
			}
		}

		@Override
		public void onRetry(int retryNo) {
			LogUtil.warn("Platform http connection retries. Retry time: " + retryNo);
		}
	};
	
	private String constructPlanUrl(double startLat, double startLng,
			double endLat, double endLng) {
		return String
				.format("%s/Ability/services/ocs/plan/%f/%f/%f/%f/20.0/0.013888888888888888/120.0/0/12345",
						IOV_HOST, startLng, startLat, endLng, endLat);
	}
	
	public void planPath(double startLat, double startLng, double endLat, double endLng) {
		String url = constructPlanUrl(startLat, startLng, endLat, endLng);
		LogUtil.verbose("begin plan path.");
		LogUtil.verbose("startPoint: " + startLat + " " + startLng);
		LogUtil.verbose("destination point: " + endLat + " " + endLng);
		LogUtil.verbose("Url: " + url);
		client.setMaxRetriesAndTimeout(MAX_RETRY_TIMES, SERVICE_TIMEOUT);
		client.get(context, url, planResHandler);
	}
}

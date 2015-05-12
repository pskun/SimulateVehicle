package edu.bupt.sv.service;

import edu.bupt.sv.utils.LogUtil;
import android.content.Context;
import android.os.Handler;

public class PlatformAccessor implements Runnable, NetworkConstants {

	private Context context;
	private Handler coreHandler;

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	private String constructPlanUrl(double startLat, double startLng,
			double endLat, double endLng) {
		return String
				.format("%s/Ability/services/ocs/plan/%f/%f/%f/%f/20.0/0.013888888888888888/120.0/0/12345",
						IOV_HOST, startLng, startLat, endLng, endLat);
	}

	public void plan(double startLat, double startLng, double endLat, double endLng) {
		LogUtil.verbose("test");
//		String url = constructPlanUrl(double startLat, double startLng, double endLat, double endLng);
//		LogUtil.info(url);
//		mAsyncHttpClient.get(mContext, url, new JsonHttpResponseHandler() {
//			@Override
//			public void onSuccess(int statusCode, Header[] headers,
//					JSONObject jo) {
//				try {
//					JSONArray ja = jo.getJSONArray("pathNode");
//
//					JSONArray links = jo.getJSONArray("links");
//					Location[] points = new Location[ja.length()];
//					for (int i = 0; i < ja.length(); i++) {
//						points[i] = new Location();
//						points[i].setLatitude(ja.getJSONObject(i).getDouble(
//								"latitude"));
//						points[i].setLongitude(ja.getJSONObject(i).getDouble(
//								"longitude"));
//						if (i != 0) {
//							points[i].setLink(links.getLong(i - 1));
//						}
//					}
//					cb.onSuccess(points);
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//
//			@Override
//			public void onFailure(int statusCode, Header[] headers,
//					Throwable throwable, JSONObject errorResponse) {
//				toastError(errorResponse);
//			}
//		});
	}
}

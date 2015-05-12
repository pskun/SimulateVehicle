package edu.bupt.sv.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class LogUtil {
	private final static int LEBEL_NONE = 0;
	private final static int LEVEL_VERBOSE = 1;
	private final static int LEVEL_WARN = 2;
	private final static int LEVEL_ERROR = 3;
	
	private static int DEBUG_SWITCH = LEVEL_VERBOSE;
	
	private final static String TAG = "LogUtils";
	// ÊÇ·ñÐ´µ½SD¿¨ TODO
	private static boolean toFile = false;
	
	public static void toast(Context context, String message) {
		if (context == null) {
			return;
		}
		Toast.makeText(context, message, 0).show();
	}
	
	public static void verbose(String tag, String msg) {
		if (DEBUG_SWITCH >= LEVEL_VERBOSE)
			Log.v(tag, msg);
	}
	
	public static void verbose(String msg) {
		verbose(TAG, msg);
	}
	
	public static void warn(String tag, String msg) {
		if (DEBUG_SWITCH >= LEVEL_WARN)
			Log.w(tag, msg);
	}
	
	public static void warn(String msg) {
		verbose(TAG, msg);
	}
	
	public static void error(String tag, String msg) {
		if (DEBUG_SWITCH >= LEVEL_ERROR)
			Log.e(tag, msg);
	}
	
	public static void error(String msg) {
		verbose(TAG, msg);
	}
}

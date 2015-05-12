package edu.bupt.sv.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class LogUtil {
	private final static String TAG = "LogUtils";
	//  «∑Ò–¥µΩSDø® TODO
	private static boolean toFile = false;
	
	public static void toast(Context context, String message) {
		if (context == null) {
			return;
		}
		Toast.makeText(context, message, 0).show();
	}
	
	public static void verbose(String tag, String msg)
	{
		Log.v(tag, msg);
	}
	
	public static void verbose(String msg) {
		verbose(TAG, msg);
	}
	
	public static void info(String tag, String msg)
	{
		Log.i(tag, msg);
	}
	
	public static void info(String msg) {
		verbose(TAG, msg);
	}
	
	public static void warn(String tag, String msg)
	{
		Log.w(tag, msg);
	}
	
	public static void warn(String msg) {
		verbose(TAG, msg);
	}
	
	public static void error(String tag, String msg)
	{
		Log.e(tag, msg);
	}
	
	public static void error(String msg) {
		verbose(TAG, msg);
	}
}

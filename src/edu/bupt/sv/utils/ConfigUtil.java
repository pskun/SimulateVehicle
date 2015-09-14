package edu.bupt.sv.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 提供对常用配置的存取
 * 采用SharedPreferences存储方式
 * @author pankunhao
 *
 */

public class ConfigUtil {
	private static final String CONFIG_PREFERENCE_NAME = "tmconfig";
	
	private static final String TM_HOST_KEY = "tmhost";
	private static final String TM_PORT_KEY = "tmport";
	private static final String IOV_ADDR_KEY = "iovaddr";
	
	private static SharedPreferences getPreference(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(CONFIG_PREFERENCE_NAME, Activity.MODE_PRIVATE);
		return sp;
	}
	
	private static Editor getPreferenceEditor(Context ctx) {
		SharedPreferences sp = getPreference(ctx);
		Editor editor = sp.edit();
		return editor;
	}
	
	public static boolean writeTmHost(Context ctx, String ip) {
		if(CommonUtil.isStringNull(ip))
			return false;
		if(!CommonUtil.isIpv4(ip))
			return false;
		Editor editor = getPreferenceEditor(ctx);
		editor.putString(TM_HOST_KEY, ip);
		return editor.commit();
	}
	
	public static boolean writeTmPort(Context ctx, String port) {
		if(CommonUtil.isStringNull(port))
			return false;
		int p = -1;
		try {
			p = Integer.parseInt(port);
		} catch(Exception e) {
			return false;
		}
		if(p<0||p>65536) return false;
		Editor editor = getPreferenceEditor(ctx);
		editor.putInt(TM_PORT_KEY, p);
		return editor.commit();
	}
	
	public static boolean writeIovAddr(Context ctx, String addr) {
		if(CommonUtil.isStringNull(addr))
			return false;
		Editor editor = getPreferenceEditor(ctx);
		editor.putString(IOV_ADDR_KEY, addr);
		return editor.commit();
	}
	
	public static String readTmHost(Context ctx) {
		SharedPreferences sp = getPreference(ctx);
		return sp.getString(TM_HOST_KEY, null);
	}
	
	public static int readTmPort(Context ctx) {
		SharedPreferences sp = getPreference(ctx);
		return sp.getInt(TM_PORT_KEY, -1);
	}
	
	public static String readIovAddr(Context ctx) {
		SharedPreferences sp = getPreference(ctx);
		return sp.getString(IOV_ADDR_KEY, null);
	}
	
}

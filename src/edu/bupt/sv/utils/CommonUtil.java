package edu.bupt.sv.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

public final class CommonUtil {

	// 判断字符串是否为空
	public static boolean isStringNull(String s){
		s = s.trim();
		return !(s!=null && s.length()>0);
	}
	
	// 连接多个对象成为字符串
	public static String catString(Object... args) {
		StringBuilder sb = new StringBuilder("");
		for(Object arg : args) {
			sb.append(arg);
		}
		return sb.toString();
	}
	
	/**
	 * TM返回的经纬度是一个整型，需要转换为浮点型
	 * @param latlng
	 * @return
	 */
	public static double convertToValidLatLng(Integer latlng) {
		Assert.assertNotNull(latlng);
		double ret = latlng.doubleValue() / 1000000;
		return ret;
	}
	
	/**
	 * 判断一个ip是否合法
	 */
	public static boolean isIpv4(String ipAddress) {
		String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
			    +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
			    +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
			    +"(00?\\d|1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";

		Pattern pattern = Pattern.compile(ip);
		Matcher matcher = pattern.matcher(ipAddress);
		return matcher.matches();
	}
	
	/**
	 * 判断一个linkId是否合法
	 * @param id
	 * @return
	 */
	public static boolean isLinkNodeIdValid(Integer id) {
		return (id!=null && id.intValue()!=0);
	}
	
	public static double milesToMeter(double mile) {
		if(mile<=0)
			return 0.0;
		return mile * 1.60931 * 1000;
	}
	
	/**
	 * 计算两点之间的直线距离
	 */
	private static final double EARTH_RADIUS = 6378.137;
	
	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}
	
	public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + 
				Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000);
		return s;
	}
}

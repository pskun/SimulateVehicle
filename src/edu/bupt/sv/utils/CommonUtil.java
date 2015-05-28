package edu.bupt.sv.utils;

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
}

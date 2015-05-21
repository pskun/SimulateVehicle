package edu.bupt.sv.utils;

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
}

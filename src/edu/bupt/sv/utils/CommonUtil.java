package edu.bupt.sv.utils;

public final class CommonUtil {

	// ÅÐ¶Ï×Ö·û´®ÊÇ·ñÎª¿Õ
	public static boolean isStringNull(String s){
		s = s.trim();
		return !(s!=null && s.length()>0);
	}
}

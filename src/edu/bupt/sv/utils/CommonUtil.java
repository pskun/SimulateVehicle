package edu.bupt.sv.utils;

public final class CommonUtil {

	// �ж��ַ����Ƿ�Ϊ��
	public static boolean isStringNull(String s){
		s = s.trim();
		return !(s!=null && s.length()>0);
	}
	
	// ���Ӷ�������Ϊ�ַ���
	public static String catString(Object... args) {
		StringBuilder sb = new StringBuilder("");
		for(Object arg : args) {
			sb.append(arg);
		}
		return sb.toString();
	}
}

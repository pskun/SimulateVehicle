package edu.bupt.sv.utils;

public final class CommonUtil {

	// �ж��ַ����Ƿ�Ϊ��
	public static boolean isStringNull(String s){
		s = s.trim();
		return !(s!=null && s.length()>0);
	}
}

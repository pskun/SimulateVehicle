package edu.bupt.sv.utils;

import junit.framework.Assert;

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
	
	/**
	 * TM���صľ�γ����һ�����ͣ���Ҫת��Ϊ������
	 * @param latlng
	 * @return
	 */
	public static double convertToValidLatLng(Integer latlng) {
		Assert.assertNotNull(latlng);
		double ret = latlng.doubleValue() / 1000000;
		return ret;
	}
	
	public static boolean isLinkNodeIdValie(Integer id) {
		return (id!=null && id.intValue()!=0);
	}
}

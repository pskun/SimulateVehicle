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
	
	/**
	 * �ж�һ��linkId�Ƿ�Ϸ�
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

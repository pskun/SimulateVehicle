package edu.bupt.sv.tm;

import java.util.List;

/**
 * ��������NTY�����ݣ���Щ�ֶη���ֵ����Ϊ�գ���Ҫ����֮ǰ���͵���Ϣ���Ͳ��ܵõ�NTY����ȷ����
 * @author pankunhao
 */
public class NTYMessage {
	public int ID;
	public long STime;
	public class T_NTY{
		public int Code;    //��Ӧ��ͨ��
		//�������
		public List<Integer> VI;
		public List<Integer> Lad;
		public List<Integer> Longd;
		public List<Double> Speed;
		public List<Double> AC;
		public List<Integer> Link;
		public List<Double> Offset;
		public List<Double> Elec;
		public List<Integer> Status;
		
		public List<Integer> RTime;
		public List<Double> BAll;
		public List<Integer> CTime;
		public List<Integer> QTime;
		//���վ���
		public List<Integer> CI;
		public List<Integer> WSNum;
		public List<Integer> LSNum;
		public List<Integer> QNum;
		//Link���
		public List<Integer> LI;
		public List<Double> TransNum;
	}
	public T_NTY NTY;
}


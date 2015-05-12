package edu.bupt.sv.tm;

import java.util.List;

/**
 * 包括所有NTY的内容，有些字段返回值可能为空，需要根据之前发送的消息类型才能得到NTY的正确类型
 * @author pankunhao
 */
public class NTYMessage {
	public int ID;
	public long STime;
	public class T_NTY{
		public int Code;    //响应吗，通用
		//车辆相关
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
		//充电站相关
		public List<Integer> CI;
		public List<Integer> WSNum;
		public List<Integer> LSNum;
		public List<Integer> QNum;
		//Link相关
		public List<Integer> LI;
		public List<Double> TransNum;
	}
	public T_NTY NTY;
}


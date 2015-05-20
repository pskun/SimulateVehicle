package edu.bupt.sv.core;

public interface MsgConstants {
	// Ԥ���Ŀ���Ϣ
	public static final int MSG_NONE = -1;
	// ��ʼ��
	public static final int MSG_INIT_THREAD = 0;
	// ��ʼ������
	public static final int MSG_INIT_VEHICLE = 1;
	// ���ĳ�����Ϣ
	public static final int MSG_SUBSCRIBE_INFO = 2;
	// ��ȡ·���滮
	public static final int MSG_PATH_PLAN = 3;
	// ���������
	public static final int MSG_REQUEST_CHARGE = 4;
	// �ı�·��
	public static final int MSG_CHANGE_PATH = 5;
	// �ı��յ�
	public static final int MSG_CHANGE_DEST = 6;
	// ��ȡ�����б�
	public static final int MSG_VEHICLE_LIST = 7;
	// ����
	public static final int MSG_ON_ERROR = 8;
	// �յ�����
	public static final int MSG_ON_RECEIVE = 9;
	// ���ٲ��˳�
	public static final int MSG_ON_QUIT	= 10;
	
	// ��������
	public static final int DATA_ERROR = 0;
	// �����б����ݱ�ʶ
	public static final int DATA_VEHICLE_LIST = 1;
	// �����������ݱ�ʶ
	public static final int DATA_VEHICLE_INFO = 2;
	// ·���滮���ݱ�ʶ
	public static final int DATA_PATH_PLAN = 3;
}

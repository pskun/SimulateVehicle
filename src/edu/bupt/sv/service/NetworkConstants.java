package edu.bupt.sv.service;

public interface NetworkConstants {

	public final String TM_HOST = "10.108.121.112";
	public final int TM_PORT = 8888;
	
	public final String IOV_HOST = "http://10.108.120.176:8080";
	
	// TM��ز���
	// TM��Ӧ��
	// �ɹ�ȡ�����ݻ�ɹ�����ָ��ͬ��
	public final int RES_SUCCESS_CODE = 200;
	// �ɹ�ȡ�ò�������
	public final int RES_PART_SUCCESS_CODE = 206;
	// ������Ϣ����
	public final int RES_ERROR_CODE = 400;
	// û����Ҫ������
	public final int RES_NO_DATA_CODE = 404;
	
	// ������������ز���
	// http����retry����
	public final int MAX_RETRY_TIMES = 3;
	// http���ӳ�ʱʱ��(����)
	public final int SERVICE_TIMEOUT = 10000;
}

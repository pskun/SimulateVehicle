package edu.bupt.sv.tm;

public interface TMListener {
	/**
	 * �յ�TM��Ϣ�Ļص�
	 * @param ID
	 * @param NTY
	 */
	void onReceiveNTY(int ID, NTYMessage NTY);
}

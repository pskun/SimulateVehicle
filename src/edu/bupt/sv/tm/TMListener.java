package edu.bupt.sv.tm;

public interface TMListener {
	/**
	 * 收到TM消息的回调
	 * @param ID
	 * @param NTY
	 */
	void onReceiveNTY(int ID, NTYMessage NTY);
}

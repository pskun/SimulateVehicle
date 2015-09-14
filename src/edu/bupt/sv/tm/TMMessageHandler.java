package edu.bupt.sv.tm;

import java.util.*;
import java.io.IOException;

import com.google.gson.*;

import edu.bupt.sv.service.NetworkConstants;
import edu.bupt.sv.utils.LogUtil;

class Results {
	public Results(int ID, String s) {
		this.ID = ID;
		this.s = s;
	}

	public int ID;
	public String s;
}

// �������еı���������������޸�
class SUBMessage {
	public SUBMessage(int ID, long STime, List<Integer> SID, String SType) {
		this.ID = ID;
		this.STime = STime;
		SUB.SType = SType;
		SUB.SID = SID;
	}

	int ID;
	long STime;

	class T_SUB {
		List<Integer> SID;
		String SType;
	}

	T_SUB SUB = new T_SUB();
}

class PUBMessage_New {
	public PUBMessage_New(int ID, long STime, List<Integer> Index,
			List<List<Integer>> LS, List<List<Integer>> CS, List<Double> Cur,
			List<Double> All) {
		this.ID = ID;
		this.STime = STime;
		PUB.Index = Index;
		PUB.Path.LS = LS;
		PUB.Path.CS = CS;
		PUB.Battery.Cur = Cur;
		PUB.Battery.All = All;
	}

	int ID;
	long STime;

	class T_PUB {
		List<Integer> Index;
		int Type = 0;

		class T_Path {
			List<List<Integer>> LS;
			List<List<Integer>> CS;
		}

		T_Path Path = new T_Path();

		class T_Battery {
			List<Double> Cur;
			List<Double> All;
		}

		T_Battery Battery = new T_Battery();
	}

	T_PUB PUB = new T_PUB();
}

class PUBMessage_Path {
	public PUBMessage_Path(int ID, long STime, List<Integer> Index,
			List<List<Integer>> LS, List<List<Integer>> CS) {
		this.ID = ID;
		this.STime = STime;
		PUB.Index = Index;
		PUB.Path.LS = LS;
		PUB.Path.CS = CS;
	}

	int ID;
	long STime;

	class T_PUB {
		List<Integer> Index;
		int Type = 1;

		class T_Path {
			List<List<Integer>> LS;
			List<List<Integer>> CS;
		}

		T_Path Path = new T_Path();
	}

	T_PUB PUB = new T_PUB();
}

class PUBMessage_Dst {
	public PUBMessage_Dst(int ID, long STime, List<Integer> Index,
			List<Integer> DID) {
		this.ID = ID;
		this.STime = STime;
		PUB.Index = Index;
		PUB.Dst.DID = DID;
	}

	int ID;
	long STime;

	class T_PUB {
		List<Integer> Index;
		int Type = 2;

		class T_Dst {
			List<Integer> DID;
		}

		T_Dst Dst = new T_Dst();
	}

	T_PUB PUB = new T_PUB();
}

public class TMMessageHandler implements NetworkConstants {
	static Client client = null;
	
	public boolean initialize(String host, int port) {
		try {
			client = new Client(TM_HOST, TM_PORT);
		} catch (Exception e) {
			client = null;
			LogUtil.verbose("initialize TMMessageHandler failed.");
			return false;
		}
		return true;
	}

	public void close() {

		if (client != null) {
			try {
				client.closeReceive();
				client.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			client = null;
		}
	}

	/**
	 * ���Ͷ������г�����Ϣ
	 * @deprecated
	 * @return ���ͳɹ����ط���ID�����򷵻�-1
	 */
	public int sendSubAllVehicle(int size) 
	{
		if (size > 300) size = 300;
		List<Integer> VI = new ArrayList<Integer>();
		for (int i = 0; i < size; i++)
			VI.add(i);
		Results results = JsonMessageSend.sendSubVehicle(VI);
		String message_send = results.s;
		if (client.send(message_send))
			return results.ID;
		return -1;
	}

	/**
	 * ���ͳ������ĳ�����Ϣ
	 * @param VI ����ID�б�
	 * @return
	 */
	public int sendSubVehicleConstantly(List<Integer> VI)
	{
		Results results = JsonMessageSend.sendSubVehicleConstantly(VI);
		String message_send = results.s;
		if (client.send(message_send))
			return results.ID;
		return -1;
	}

	/**
	 * ���Ͷ���CI���վ��Ϣ
	 * @deprecated
	 * @param CI ���վId
	 * @return
	 */
	public int sendSubCharge(List<Integer> CI) // 
	{
		Results results = JsonMessageSend.sendSubCharge(CI);
		String message_send = results.s;
		if (client.send(message_send))
			return results.ID;
		return -1;
	}

	/**
	 * ���Ͷ�������Links�Ľ�ͨ��Ϣ
	 * @deprecated
	 * @return
	 */
	public int sendGetAllLinkTrans()
	{
		Results results = JsonMessageSend.sendSubAllLink();
		String message_send = results.s;
		if (client.send(message_send))
			return results.ID;
		return -1;
	}

	/**
	 * ���Ͷ��Ĳ���Links�Ľ�ͨ��Ϣ
	 * @deprecated
	 * @param links
	 * @return
	 */
	public int sendGetLinkTrans(List<Integer> links)
	{
		Results results = JsonMessageSend.sendSubLink(links);
		String message_send = results.s;
		if (client.send(message_send))
			return results.ID;
		return -1;
	}

	/**
	 * �������󣬷ǳ�������
	 * @param VI
	 * @return
	 */
	public int sendSubVehicleInfo(List<Integer> VI) // ���Ͷ��ĳ���ͳ����Ϣ������Ϣ
	{
		Results results = JsonMessageSend.sendSubVInfo(VI); // ���ɴ�������Ϣ
		String message_send = results.s;
		if (client.send(message_send))
			return results.ID;
		return -1;
	}

	/**
	 * ���ʹ���������Ϣ
	 * @param Index ����ID
	 * @param LS �滮��·��link id
	 * @param CS ;���ĳ��վid
	 * @param CurBattery ��ǰ����
	 * @param AllBattery �ܵ���
	 * @return
	 */
	public int sendNewVehicle(List<Integer> Index, List<List<Integer>> LS,
			List<List<Integer>> CS, List<Double> CurBattery,
			List<Double> AllBattery)
	{
		Results results = JsonMessageSend.sendPubNew(Index, LS, CS, CurBattery,
				AllBattery);
		String message_send = results.s;
		if (client.send(message_send))
			return results.ID;
		return -1;
	}

	/**
	 * ���͸ı�·������Ϣ
	 * @param Index ����ID
	 * @param LS link id �б�
	 * @param CS node id �б������б���
	 * @return
	 */
	public int sendChangePath(List<Integer> Index, List<List<Integer>> LS,
			List<List<Integer>> CS)
	{
		Results results = JsonMessageSend.sendPubPath(Index, LS, CS);
		String message_send = results.s;
		if (client.send(message_send))
			return results.ID;
		return -1;
	}

	/**
	 * ���͸ı��յ���Ϣ
	 * @param Index ����ID
	 * @param DID �յ��node id
	 * @return
	 */
	public int sendChangeDst(List<Integer> Index, List<Integer> DID)
	{
		Results results = JsonMessageSend.sendPubDst(Index, DID);
		String message_send = results.s;
		if (client.send(message_send))
			return results.ID;
		return -1;
	}

	public TMMessageHandler(final TMListener handler, String host, int port) throws Exception {
		boolean initOK = true;
		if (null == client) {
			if(!initialize(host, port)) {
				LogUtil.error("TMMessageHandler initialize failed.");
				// System.exit(1);
				throw new Exception("TM initialize failed.");
			}
		}
		new Thread(new Runnable() {
			public void run() {
				while (client != null) {
					String jMessage;
					try {
						jMessage = new String(client.receive());
						if (jMessage != null) {
							NTYMessage NTY = new Gson().fromJson(jMessage,
									NTYMessage.class);
							handler.onReceiveNTY(NTY.ID, NTY);
						}
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		}).start();
		LogUtil.verbose("TMMessageHandler: initialize TMMessageHandler done.");
	}
/*
	public static void main(String[] args) {
		TMMessageHandler jm = new TMMessageHandler(new TMListener() {
			@Override
			public void onReceiveNTY(int ID, NTYMessage NTY) {
				System.out.println("ID = " + ID);
				System.out.print("VI = ");
				for (Integer i : NTY.NTY.VI)
					System.out.print(i + ",");
				System.out.print("\nLad = ");
				if (NTY.NTY.Lad != null)
					for (Integer i : NTY.NTY.Lad)
						System.out.print(i + ",");
				System.out.print("\nLongd = ");
				if (NTY.NTY.Longd != null)
					for (Integer i : NTY.NTY.Longd)
						System.out.print(i + ",");
				System.out.print("\nSpeed = ");
				if (NTY.NTY.Speed != null)
					for (Double i : NTY.NTY.Speed)
						System.out.print(i + ",");
				System.out.print("\nAC = ");
				if (NTY.NTY.AC != null)
					for (Double i : NTY.NTY.AC)
						System.out.print(i + ",");
				System.out.print("\nLink = ");
				if (NTY.NTY.Link != null)
					for (Integer i : NTY.NTY.Link)
						System.out.print(i + ",");
				System.out.print("\nOffset = ");
				if (NTY.NTY.Offset != null)
					for (Double i : NTY.NTY.Offset)
						System.out.print(i + ",");
				System.out.print("\nElec = ");
				if (NTY.NTY.Elec != null)
					for (Double i : NTY.NTY.Elec)
						System.out.print(i + ",");
				System.out.print("\nStatus = ");
				if (NTY.NTY.Status != null)
					for (Integer i : NTY.NTY.Status)
						System.out.print(i + ",");
				System.out.println();
			}
		});
		jm.sendSubAllVehicle(300);
		List<Integer> li = new ArrayList<Integer>();
		li.add(1);
		jm.sendSubVehicleConstantly(li);
	}
*/
}

class JsonMessageSend {
	private JsonMessageSend() {
	} // ��ֹʵ����

	private static int ID = 0;

	private static int getID() {
		ID = (ID + 1) % Integer.MAX_VALUE;
		return ID;
	}

	public static Results sendSubVehicle(List<Integer> VI) { // ���ɳ���������Ϣ
		int ID = getID();
		SUBMessage subMessage = new SUBMessage(ID, System.currentTimeMillis(),
				VI, "Vehicle");
		return new Results(ID, (new Gson()).toJson(subMessage));
	}

	public static Results sendSubVehicleConstantly(List<Integer> VI) { // ���ɳ������ĳ�����Ϣ
		int ID = getID();
		SUBMessage subMessage = new SUBMessage(ID, System.currentTimeMillis(),
				VI, "VehicleS");
		return new Results(ID, (new Gson()).toJson(subMessage));
	}

	public static Results sendSubCharge(List<Integer> CI) { // ���ɳ��վ������Ϣ
		int ID = getID();
		SUBMessage subMessage = new SUBMessage(ID, System.currentTimeMillis(),
				CI, "Charge");
		return new Results(ID, (new Gson()).toJson(subMessage));
	}

	public static Results sendSubLink(List<Integer> LI) { // ���ɵ�·������Ϣ
		int ID = getID();
		SUBMessage subMessage = new SUBMessage(ID, System.currentTimeMillis(),
				LI, "Link");
		return new Results(ID, (new Gson()).toJson(subMessage));
	}

	public static Results sendSubAllLink() { // ���ɶ������е�·��Ϣ
		int ID = getID();
		List<Integer> LI = new ArrayList<Integer>();
		SUBMessage subMessage = new SUBMessage(ID, System.currentTimeMillis(),
				LI, "Link");
		return new Results(ID, (new Gson()).toJson(subMessage));
	}

	public static Results sendSubVInfo(List<Integer> VI) { // ���ɳ���ͳ����Ϣ������Ϣ
		int ID = getID();
		SUBMessage subMessage = new SUBMessage(ID, System.currentTimeMillis(),
				VI, "VInfo");
		return new Results(ID, (new Gson()).toJson(subMessage));
	}

	// ���ɽ����³�����Ϣ
	public static Results sendPubNew(List<Integer> Index,
			List<List<Integer>> LS, List<List<Integer>> CS, List<Double> Cur,
			List<Double> All) {
		int ID = getID();
		PUBMessage_New pubMessage = new PUBMessage_New(ID,
				System.currentTimeMillis(), Index, LS, CS, Cur, All);
		return new Results(ID, (new Gson().toJson(pubMessage)));
	}

	// ���ɸı䳵��·����Ϣ
	public static Results sendPubPath(List<Integer> Index,
			List<List<Integer>> LS, List<List<Integer>> CS) {
		int ID = getID();
		PUBMessage_Path pubMessage = new PUBMessage_Path(ID,
				System.currentTimeMillis(), Index, LS, CS);
		return new Results(ID, (new Gson().toJson(pubMessage)));
	}

	// ���ɸı䳵���յ���Ϣ
	public static Results sendPubDst(List<Integer> Index, List<Integer> DID) {
		int ID = getID();
		PUBMessage_Dst pubMessage = new PUBMessage_Dst(ID,
				System.currentTimeMillis(), Index, DID);
		return new Results(ID, (new Gson().toJson(pubMessage)));
	}
}

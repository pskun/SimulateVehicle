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

// 以下类中的变量名均不能随便修改
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
	 * 发送订阅所有车辆消息
	 * @deprecated
	 * @return 发送成功返回发送ID，否则返回-1
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
	 * 发送持续订阅车辆消息
	 * @param VI 车辆ID列表
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
	 * 发送订阅CI充电站消息
	 * @deprecated
	 * @param CI 充电站Id
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
	 * 发送订阅所有Links的交通信息
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
	 * 发送订阅部分Links的交通信息
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
	 * 单次请求，非持续订阅
	 * @param VI
	 * @return
	 */
	public int sendSubVehicleInfo(List<Integer> VI) // 发送订阅车辆统计消息订阅消息
	{
		Results results = JsonMessageSend.sendSubVInfo(VI); // 生成待发送消息
		String message_send = results.s;
		if (client.send(message_send))
			return results.ID;
		return -1;
	}

	/**
	 * 发送创建车辆消息
	 * @param Index 车辆ID
	 * @param LS 规划的路径link id
	 * @param CS 途径的充电站id
	 * @param CurBattery 当前电量
	 * @param AllBattery 总电量
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
	 * 发送改变路径的消息
	 * @param Index 车辆ID
	 * @param LS link id 列表
	 * @param CS node id 列表，传空列表即可
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
	 * 发送改变终点消息
	 * @param Index 车辆ID
	 * @param DID 终点的node id
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
	} // 禁止实例化

	private static int ID = 0;

	private static int getID() {
		ID = (ID + 1) % Integer.MAX_VALUE;
		return ID;
	}

	public static Results sendSubVehicle(List<Integer> VI) { // 生成车辆订阅消息
		int ID = getID();
		SUBMessage subMessage = new SUBMessage(ID, System.currentTimeMillis(),
				VI, "Vehicle");
		return new Results(ID, (new Gson()).toJson(subMessage));
	}

	public static Results sendSubVehicleConstantly(List<Integer> VI) { // 生成持续订阅车辆消息
		int ID = getID();
		SUBMessage subMessage = new SUBMessage(ID, System.currentTimeMillis(),
				VI, "VehicleS");
		return new Results(ID, (new Gson()).toJson(subMessage));
	}

	public static Results sendSubCharge(List<Integer> CI) { // 生成充电站订阅消息
		int ID = getID();
		SUBMessage subMessage = new SUBMessage(ID, System.currentTimeMillis(),
				CI, "Charge");
		return new Results(ID, (new Gson()).toJson(subMessage));
	}

	public static Results sendSubLink(List<Integer> LI) { // 生成道路订阅消息
		int ID = getID();
		SUBMessage subMessage = new SUBMessage(ID, System.currentTimeMillis(),
				LI, "Link");
		return new Results(ID, (new Gson()).toJson(subMessage));
	}

	public static Results sendSubAllLink() { // 生成订阅所有道路消息
		int ID = getID();
		List<Integer> LI = new ArrayList<Integer>();
		SUBMessage subMessage = new SUBMessage(ID, System.currentTimeMillis(),
				LI, "Link");
		return new Results(ID, (new Gson()).toJson(subMessage));
	}

	public static Results sendSubVInfo(List<Integer> VI) { // 生成车辆统计信息订阅消息
		int ID = getID();
		SUBMessage subMessage = new SUBMessage(ID, System.currentTimeMillis(),
				VI, "VInfo");
		return new Results(ID, (new Gson()).toJson(subMessage));
	}

	// 生成建立新车辆消息
	public static Results sendPubNew(List<Integer> Index,
			List<List<Integer>> LS, List<List<Integer>> CS, List<Double> Cur,
			List<Double> All) {
		int ID = getID();
		PUBMessage_New pubMessage = new PUBMessage_New(ID,
				System.currentTimeMillis(), Index, LS, CS, Cur, All);
		return new Results(ID, (new Gson().toJson(pubMessage)));
	}

	// 生成改变车辆路径消息
	public static Results sendPubPath(List<Integer> Index,
			List<List<Integer>> LS, List<List<Integer>> CS) {
		int ID = getID();
		PUBMessage_Path pubMessage = new PUBMessage_Path(ID,
				System.currentTimeMillis(), Index, LS, CS);
		return new Results(ID, (new Gson().toJson(pubMessage)));
	}

	// 生成改变车辆终点消息
	public static Results sendPubDst(List<Integer> Index, List<Integer> DID) {
		int ID = getID();
		PUBMessage_Dst pubMessage = new PUBMessage_Dst(ID,
				System.currentTimeMillis(), Index, DID);
		return new Results(ID, (new Gson().toJson(pubMessage)));
	}
}

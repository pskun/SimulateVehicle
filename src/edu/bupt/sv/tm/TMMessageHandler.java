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
	
	public boolean initialize() {
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

	public int sendGetAllLinkTrans() // 发送订阅所有Links的交通信息
	{
		Results results = JsonMessageSend.sendSubAllLink();
		String message_send = results.s;
		if (client.send(message_send))
			return results.ID;
		return -1;
	}

	public int sendGetLinkTrans(List<Integer> links) // 发送订阅部分Links的交通信息
	{
		Results results = JsonMessageSend.sendSubLink(links);
		String message_send = results.s;
		if (client.send(message_send))
			return results.ID;
		return -1;
	}

	public int sendSubVehicleInfo(List<Integer> VI) // 发送订阅车辆统计消息订阅消息
	{
		Results results = JsonMessageSend.sendSubVInfo(VI); // 生成待发送消息
		String message_send = results.s;
		if (client.send(message_send))
			return results.ID;
		return -1;
	}

	public int sendNewVehicle(List<Integer> Index, List<List<Integer>> LS,
			List<List<Integer>> CS, List<Double> CurBattery,
			List<Double> AllBattery) // 发送创建车辆消息
	{
		Results results = JsonMessageSend.sendPubNew(Index, LS, CS, CurBattery,
				AllBattery);
		String message_send = results.s;
		if (client.send(message_send))
			return results.ID;
		return -1;
	}

	public int sendChangePath(List<Integer> Index, List<List<Integer>> LS,
			List<List<Integer>> CS)
	// 发送改变路径消息
	{
		Results results = JsonMessageSend.sendPubPath(Index, LS, CS);
		String message_send = results.s;
		if (client.send(message_send))
			return results.ID;
		return -1;
	}

	public int sendChangeDst(List<Integer> Index, List<Integer> DID) // 发送改变终点消息
	{
		Results results = JsonMessageSend.sendPubDst(Index, DID);
		String message_send = results.s;
		if (client.send(message_send))
			return results.ID;
		return -1;
	}

	public TMMessageHandler(final TMListener handler) {
		if (null == client) {
			if(!initialize()) {
				LogUtil.error("TMMessageHandler initialize failed.");
				System.exit(1);
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
		/*
		 * Map<Integer, CarRecord> info = new HashMap<Integer, CarRecord> ();
		 * for (int i = 0; i < 10; i++){ info.put(i, new CarRecord(i)); }
		 * updateVehicle (info); for (int i = 0; i < 10; i++){ CarRecord temp =
		 * info.get(i);
		 * System.out.println(temp.getCarId()+" "+temp.getLatitude()
		 * +" "+temp.getLinkId()+" "
		 * +temp.getLongtitude()+" "+temp.getOffset()+" "
		 * +temp.getRemainEnergy()+" "+temp.getSpeed()+" "
		 * +temp.getStatus()+" "+temp.getTime()); } Map<Integer, CS> info2 = new
		 * HashMap<Integer, CS> (); for (int i = 0; i < 10; i++){ info2.put(i,
		 * new CS(i,10,10,10,10)); } updateChargeStation(info2); for (int i = 0;
		 * i < 10; i++){ CS temp = info2.get(i);
		 * System.out.println(temp.getcsId(
		 * )+" "+temp.getCapacity()+" "+temp.getFreeCount()+" "+temp.getqCount()
		 * +" "+temp.getTotalCount()); }
		 * 
		 * Map<Integer, VehicleInfo> info3 = new HashMap<Integer,
		 * VehicleInfo>(); for (int i = 0; i < 10; i++){ info3.put(i, new
		 * VehicleInfo(i)); } updateVehicleInfo(info3); for (int i = 0; i < 10;
		 * i++){ VehicleInfo temp = info3.get(i);
		 * System.out.println(temp.getCarId
		 * ()+" "+temp.getRunTime()+" "+temp.getBatteryAll
		 * ()+" "+temp.getChargeTime() +" "+temp.getQueueTime()); }
		 */
		/*
		 * try{ BufferedReader reader = new BufferedReader(new
		 * InputStreamReader(System.in)); reader.readLine(); }catch (Exception
		 * e){} Map<Integer, Double> allLinks = getLinkTrans(); for (int key :
		 * allLinks.keySet()){ double value = allLinks.get(key);
		 * System.out.println(key+"="+value); } try{ BufferedReader reader = new
		 * BufferedReader(new InputStreamReader(System.in)); reader.readLine();
		 * }catch (Exception e){} List<Integer> Cars = new ArrayList<Integer>();
		 * Cars.add(0); Cars.add(1); List< List<Integer> > Lpath = new
		 * ArrayList< List<Integer> > (); /*Integer[] path =
		 * {3221628,-3221599,-3221559
		 * ,-1692900,1692978,3221556,3219636,3221648,3221601,3219211,1694023,
		 * 1692272,3221552,3221551,3221710
		 * ,3221644,1692313,2226074,2225248,2225313,2228275,3221655 };
		 * Lpath.add(Arrays.asList(path)); Integer[] path2 =
		 * {2209901,2209911,2212327
		 * ,1686042,-3221743,3221743,1686032,1686066,3221721,1686091,
		 * 3221718,1690223,3219814,1690365,1690850,3221722,1690844,3221726};
		 * Lpath.add(Arrays.asList(path2));
		 */
		/*
		 * Integer[] path =
		 * {1682091,-3221754,-1685041,1685055,3221751,1685097,3221740
		 * ,1685162,3221735,3221729}; Lpath.add(Arrays.asList(path)); List<
		 * Integer > cpath = new ArrayList<Integer> (); List< List<Integer> >
		 * Lcpath = new ArrayList< List<Integer> >(); //Lcpath.add(cpath);
		 * cpath.add(75); Lcpath.add(cpath); Double [] bat =
		 * {29.821228853257303,16.649162545368313}; List<Double> Lbat =
		 * Arrays.asList(bat); Double [] bal = {120.0, 120.0}; List<Double> Lbal
		 * = Arrays.asList(bal); if
		 * (newVehicleAndFails(Cars,Lpath,Lcpath,Lbat,Lbal) != null){
		 * System.out.println("车辆没有新建完成"); } else System.out.println("车辆新建完成");
		 * Lpath.clear(); cpath.clear(); Lcpath.clear();
		 * 
		 * Integer[] path2 =
		 * {-3221754,-1685041,-3221752,-3221759,-1685120,1685432,3221761,
		 * 3221749,1685453, 3221745,1685169, 3221732};
		 * Lpath.add(Arrays.asList(path2)); Lcpath.add(cpath); try{
		 * BufferedReader reader = new BufferedReader(new
		 * InputStreamReader(System.in)); reader.readLine(); }catch (Exception
		 * e){} if (changePathAndFails(Cars,Lpath,Lcpath) == null)
		 * System.out.println("全部path改变完成"); else
		 * System.out.println("path没有改变完成"); /* Integer[] itemsContent =
		 * {0,1,2,3,4,5,6,7,8,9,}; List<Integer> items =
		 * Arrays.asList(itemsContent); Map<Integer, Double> someLinks =
		 * getLinkTrans(items); for (int key : someLinks.keySet()){ double value
		 * = someLinks.get(key); System.out.println(key+"="+value); } List<
		 * List<Integer> > ls = new ArrayList< List<Integer> >(), cs = new
		 * ArrayList< List<Integer> >(); List<Integer> tempList = new
		 * ArrayList<Integer>(); for (int i = 0; i < 5; i++){ cs.add(new
		 * ArrayList<Integer>(tempList)); tempList.add(i); ls.add(new
		 * ArrayList<Integer>(tempList)); } List<Double> cur = new
		 * ArrayList<Double> (), all = new ArrayList<Double> (); for (int i = 0;
		 * i < 5; i++){ cur.add(i + 0.5); all.add(i + 1.5); } if
		 * (changePathAndFails(tempList,ls,cs) == null)
		 * System.out.println("全部path改变完成"); else
		 * System.out.println("path没有改变完成"); if (changeDstAndFails(tempList,new
		 * ArrayList<Integer>(tempList)) == null)
		 * System.out.println("全部dst改变完成"); else
		 * System.out.println("dst没有改变完成"); if
		 * (newVehicleAndFails(tempList,ls,cs,cur,all) == null)
		 * System.out.println("全部车辆新建完成"); else System.out.println("车辆没有新建完成");
		 */
		// close();
	}
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

package edu.bupt.sv.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.SparseArray;
import edu.bupt.sv.entity.Link;
import edu.bupt.sv.entity.LinkRelation;
import edu.bupt.sv.entity.Node;
import edu.bupt.sv.entity.Vehicle;

public class DataConfig {
	
	private static final String LINK_INFO_FILE = "linkinfo.txt";
	private static final String NODE_INFO_FILE = "nodeinfo.txt";
	private static final String LINK_RELATION_FILE = "linkrelation.txt";
	private static final String VEHICLE_INFO_FILE = "TMConfig.txt";

	private Context mContext;

	private SparseArray<Link> linkInfo = new SparseArray<Link>();
	private SparseArray<Node> nodeInfo = new SparseArray<Node>();
	private SparseArray<LinkRelation> linkRelations = new SparseArray<LinkRelation>();
	private SparseArray<Vehicle> vehicleList = new SparseArray<Vehicle>();

	public DataConfig(Context mContext) {
		super();
		this.mContext = mContext;
	}

	public boolean initAll() {
		LogUtil.verbose("init data config.");
		LogUtil.verbose("begin to init map data");
		if(!initMapData())
			return false;
		LogUtil.verbose("begin to init vehicle data");
		return initVehicleData();
	}
	
	public SparseArray<Node> getNodeInfo() {
		return nodeInfo;
	}

	public SparseArray<Vehicle> getVehicleList() {
		return vehicleList;
	}

	public Vehicle getVehicleFromConfig(Integer vehicleId) {
		if(vehicleList == null)
			return null;
		return vehicleList.get(vehicleId.intValue());
	}
	
	public Integer getTurnLink(Integer curLinkId, int direction) {
		LinkRelation relation = linkRelations.get(curLinkId);
		if(relation == null)
			return null;
		return relation.getNextLink(direction);
	}
	
	private boolean initMapData() {
		try {
			if(!initNode()) return false;
			if(!initRelation()) return false;
			InputStreamReader in = new InputStreamReader(mContext
					.getResources().getAssets().open(LINK_INFO_FILE));
			BufferedReader bufReader = new BufferedReader(in);
			// 跳过第一行
			String line = bufReader.readLine();
			line = bufReader.readLine();
			while (line != null) {
				String[] split = line.split("\t");
				Integer id = Integer.parseInt(split[0]);
				String dir = split[1];
				Integer aNode = Integer.parseInt(split[2]);
				Integer bNode = Integer.parseInt(split[3]);
				Double length = Double.parseDouble(split[4]);
				Link link = new Link(id, aNode, bNode, length);
				linkInfo.append(link.getId(), link);
				if (!CommonUtil.isStringNull(dir)) {
					Link link1 = new Link(0 - id, bNode, aNode, length);
					linkInfo.append(link1.getId(), link1);
				}
				line = bufReader.readLine();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean initNode() {
		InputStreamReader in;
		try {
			in = new InputStreamReader(mContext.getResources().getAssets()
					.open(NODE_INFO_FILE));
			BufferedReader bufReader = new BufferedReader(in);
			// 跳过第一行
			String line = bufReader.readLine();
			line = bufReader.readLine();
			while (line != null) {
				String[] split = line.split("\t");
				Integer id = Integer.parseInt(split[0]);
				Double Lon = Double.parseDouble(split[1]);
				Double Lat = Double.parseDouble(split[2]);
				Node node = new Node(id, Lon, Lat);
				nodeInfo.append(node.getId(), node);
				line = bufReader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean initRelation() {
		InputStreamReader in;
		try {
			in = new InputStreamReader(mContext.getResources().getAssets()
					.open(LINK_RELATION_FILE));
			BufferedReader bufReader = new BufferedReader(in);
			String line = bufReader.readLine();
			line = bufReader.readLine();

			while (line != null) {
				String[] split = line.split("\t");
				Integer id = Integer.parseInt(split[0]);
				Integer[] nextlinks = new Integer[3];
				for (int i = 0; i < split.length - 1; i++) {
					if (!CommonUtil.isStringNull(split[i + 1])) {
						nextlinks[i] = Integer.parseInt(split[i + 1]);
					}
				}
				LinkRelation linkrelation = new LinkRelation(id, nextlinks);
				linkRelations.append(linkrelation.getId(), linkrelation);
				line = bufReader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean initVehicleData() {
		try {
			InputStreamReader inPut = new InputStreamReader(mContext
					.getResources().getAssets().open(VEHICLE_INFO_FILE));
			BufferedReader bufReader = new BufferedReader(inPut);
			String line = bufReader.readLine();
			line = bufReader.readLine();
			while (line != null) {
				String[] split = line.split("\t");
				Integer id = Integer.parseInt(split[0]);
				Integer startPos = Integer.parseInt(split[1]);
				Integer endPos = Integer.parseInt(split[2]);
				Integer linkID = Integer.parseInt(split[3]);
				Integer status = Integer.parseInt(split[4]);
				Integer model = Integer.parseInt(split[5]);
				Double energyCost = Double.parseDouble(split[6]);
				Double totalEnergy = Double.parseDouble(split[7]);
				Double charge = Double.parseDouble(split[8]);
				Double reservedEnergy = Double.parseDouble(split[9]);
				Double speed = Double.parseDouble(split[10]);
				line = bufReader.readLine();
				String[] split1 = line.split(" ");
				List<Integer> path = new ArrayList<Integer>();
				for (int i = 0; i < split1.length; i++) {
					path.add(Integer.parseInt(split1[i]));
				}
				// 都走不存储的暂停点
				line = bufReader.readLine();

				Vehicle vehicle = new Vehicle(id, startPos, endPos, linkID,
						status, model, energyCost, totalEnergy, charge,
						reservedEnergy, speed, path);
				System.out.print("Vehicle id: " + id);
				vehicleList.append(vehicle.getId(), vehicle);
				line = bufReader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}

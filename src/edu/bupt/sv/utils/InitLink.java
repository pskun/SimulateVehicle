package edu.bupt.sv.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.util.SparseArray;
import edu.bupt.sv.entity.Link;
import edu.bupt.sv.entity.LinkRelation;
import edu.bupt.sv.entity.Node;
import edu.bupt.sv.entity.Vehicle;

public class InitLink {
	private Context mContext;
	private SparseArray  linkInfo=new SparseArray<Link>();
	private SparseArray  nodeInfo=new SparseArray<Node>();
	public SparseArray  linkRelation=new SparseArray<LinkRelation>();
	private SparseArray  vehicleList = new SparseArray<Vehicle>();
	
	
	public InitLink(Context mContext) {
		super();
		// TODO Auto-generated constructor stub
		this.mContext=mContext;
	}

	public void initMapData(){
		 
		 try {
			 
			 this.initNode();
			 this.initRelation();
			 InputStreamReader in = new InputStreamReader(mContext.getResources().getAssets().open("linkinfo.txt"));
			 BufferedReader bufReader = new BufferedReader(in);
			 String line = bufReader.readLine();
			 line = bufReader.readLine();
			 while(line!=null){
				 String[] split = line.split("\t");
				 Integer id = Integer.parseInt(split[0]);
				 String  dir = split[1];
				 Integer aNode = Integer.parseInt(split[2]);
				 Integer bNode = Integer.parseInt(split[3]);
				 Double  length = Double.parseDouble(split[4]);
				 Link link = new Link(id,aNode,bNode,length);
				 linkInfo.append(link.getId(),link);
				 if(!CommonUtil.isStringNull(dir)){
					 Link link1 = new Link(0-id,bNode,aNode,length);
					 linkInfo.append(link1.getId(),link1);
				 }
				 line = bufReader.readLine();	
				}
					
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
	}
	
	private void initNode(){
		InputStreamReader in;
		try {
			in = new InputStreamReader(mContext.getResources().getAssets().open("nodeinfo.txt"));
			BufferedReader bufReader = new BufferedReader(in);
			String line = bufReader.readLine();
			line = bufReader.readLine();
			while(line!=null){
				//System.out.println(line);
				String[] split = line.split("\t");
				Integer id = Integer.parseInt(split[0]);
				Double  Lon = Double.parseDouble(split[1]);
				Double  Lat = Double.parseDouble(split[2]);
				Node node = new Node(id,Lon,Lat);
				nodeInfo.append(node.getId(),node);	
				line = bufReader.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	private void initRelation(){
		InputStreamReader in;
		try {
			in = new InputStreamReader(mContext.getResources().getAssets().open("linkrelation.txt"));
			BufferedReader bufReader = new BufferedReader(in);
			String line = bufReader.readLine();
			line = bufReader.readLine();
			
			while(line!=null){
				String[] split = line.split("\t");
				Integer id = Integer.parseInt(split[0]);
				Integer[] nextlinks = new Integer[3];
				for(int i=0;i<split.length-1;i++){
					if(!CommonUtil.isStringNull(split[i+1])){
					    nextlinks[i] = Integer.parseInt(split[i+1]);
					}
				}			
				LinkRelation linkrelation= new LinkRelation(id,nextlinks);			
				linkRelation.append(linkrelation.getId(),linkrelation);
				line = bufReader.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}
	
	public void initVehicleData(){
		try {
	    InputStreamReader inPut = new InputStreamReader(mContext.getResources().getAssets().open("TMConfig.txt"));
	    BufferedReader bufReader = new BufferedReader(inPut);
		String line = bufReader.readLine();
		line = bufReader.readLine();
		while(line!=null){
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
			String[] split1 = line.split("\t");
			ArrayList path = new ArrayList();
			for(int i=0;i<split1.length;i++){
				path.add(Integer.parseInt(split1[i]));
			}
			//都走不存储的暂停点
			line = bufReader.readLine();
			
			Vehicle vehicle = new Vehicle(id,startPos,endPos,linkID,status,model,energyCost,totalEnergy,charge,reservedEnergy,speed,path);
			vehicleList.append(vehicle.getId(),vehicle);	
			line = bufReader.readLine();
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

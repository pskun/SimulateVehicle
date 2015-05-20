package edu.bupt.sv.entity;
import java.util.ArrayList;

public class Vehicle {
	private Integer id;
	//起始link
	private Integer startPos;
	private Integer endPos;
	//当前所在link
	private Integer linkID;
	//车辆状态
	private Integer status;
	private Integer model;
	private Double energyCost;
	private Double totalEnergy;
	//当前电量
	private Double charge;
	private Double reservedEnergy;
	private Double speed;
	private ArrayList path;	
	// 经度
	private double longitude;
	// 纬度
	private double latitude;
	
	

	public Vehicle(Integer id, Integer startPos, Integer endPos,
			Integer linkID, Integer status, Integer model, Double energyCost,
			Double totalEnergy, Double charge, Double reservedEnergy,
			Double speed, ArrayList path) {
		super();
		this.id = id;
		this.startPos = startPos;
		this.endPos = endPos;
		this.linkID = linkID;
		this.status = status;
		this.model = model;
		this.energyCost = energyCost;
		this.totalEnergy = totalEnergy;
		this.charge = charge;
		this.reservedEnergy = reservedEnergy;
		this.speed = speed;
		this.path = path;
	}
	
	public Vehicle(){};
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getStartPos() {
		return startPos;
	}
	public void setStartPos(Integer startPos) {
		this.startPos = startPos;
	}
	public Integer getEndPos() {
		return endPos;
	}
	public void setEndPos(Integer endPos) {
		this.endPos = endPos;
	}
	public Integer getLinkID() {
		return linkID;
	}
	public void setLinkID(Integer linkID) {
		this.linkID = linkID;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getModel() {
		return model;
	}
	public void setModel(Integer model) {
		this.model = model;
	}
	public Double getEnergyCost() {
		return energyCost;
	}
	public void setEnergyCost(Double energyCost) {
		this.energyCost = energyCost;
	}
	public Double getTotalEnergy() {
		return totalEnergy;
	}
	public void setTotalEnergy(Double totalEnergy) {
		this.totalEnergy = totalEnergy;
	}
	public Double getCharge() {
		return charge;
	}
	public void setCharge(Double charge) {
		this.charge = charge;
	}
	public Double getReservedEnergy() {
		return reservedEnergy;
	}
	public void setReservedEnergy(Double reservedEnergy) {
		this.reservedEnergy = reservedEnergy;
	}
	public Double getSpeed() {
		return speed;
	}
	public void setSpeed(Double speed) {
		this.speed = speed;
	}
	public ArrayList getPath() {
		return path;
	}
	public void setPath(ArrayList path) {
		this.path = path;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	
	
}

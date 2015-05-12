package edu.bupt.sv.entity;

public class Vehicle {
	// 经度
	private double longitude;
	// 纬度
	private double latitude;
	// 速度
	private double speed;
	// 电量
	private double charge;
	// 当前所在link
	private long linkID;
	
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
	
	public double getSpeed() {
		return speed;
	}
	
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	public double getCharge() {
		return charge;
	}
	
	public void setCharge(double charge) {
		this.charge = charge;
	}
	
	public long getLinkID() {
		return linkID;
	}
	
	public void setLinkID(long linkID) {
		this.linkID = linkID;
	}
	
}

package edu.bupt.sv.entity;

public class Vehicle {
	// ����
	private double longitude;
	// γ��
	private double latitude;
	// �ٶ�
	private double speed;
	// ����
	private double charge;
	// ��ǰ����link
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

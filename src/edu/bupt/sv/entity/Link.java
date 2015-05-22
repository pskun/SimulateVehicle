package edu.bupt.sv.entity;

public class Link {

	private Integer id;
	private Integer Anode;
	private Integer Bnode;
	private double length;
	
	public Link(int id, Integer anode, Integer bnode, double length) {
		super();
		this.id = id;
		Anode = anode;
		Bnode = bnode;
		this.length = length;
		
	}
	
	public int getId() {
		return id;
	}

	public Integer getAnode() {
		return Anode;
	}

	public Integer getBnode() {
		return Bnode;
	}

	public double getLength() {
		return length;
	}
	
	
}
